package com.fleencorp.feen.review.service.impl;

import com.fleencorp.base.model.request.search.SearchRequest;
import com.fleencorp.base.model.view.search.SearchResult;
import com.fleencorp.feen.exception.base.FailedOperationException;
import com.fleencorp.feen.exception.stream.StreamNotFoundException;
import com.fleencorp.feen.like.service.LikeService;
import com.fleencorp.feen.model.domain.chat.ChatSpace;
import com.fleencorp.feen.model.domain.stream.FleenStream;
import com.fleencorp.feen.review.constant.ReviewParentType;
import com.fleencorp.feen.review.exception.core.CannotAddReviewIfStreamHasNotStartedException;
import com.fleencorp.feen.review.exception.core.ReviewNotFoundException;
import com.fleencorp.feen.review.mapper.ReviewMapper;
import com.fleencorp.feen.review.model.domain.Review;
import com.fleencorp.feen.review.model.dto.AddReviewDto;
import com.fleencorp.feen.review.model.dto.UpdateReviewDto;
import com.fleencorp.feen.review.model.holder.ReviewOtherDetailsHolder;
import com.fleencorp.feen.review.model.request.ReviewSearchRequest;
import com.fleencorp.feen.review.model.response.AddReviewResponse;
import com.fleencorp.feen.review.model.response.DeleteReviewResponse;
import com.fleencorp.feen.review.model.response.ReviewResponse;
import com.fleencorp.feen.review.model.response.UpdateReviewResponse;
import com.fleencorp.feen.review.model.search.ReviewSearchResult;
import com.fleencorp.feen.review.repository.ReviewRepository;
import com.fleencorp.feen.review.service.ReviewService;
import com.fleencorp.feen.service.stream.common.StreamService;
import com.fleencorp.feen.user.model.domain.Member;
import com.fleencorp.feen.user.model.security.RegisteredUser;
import com.fleencorp.feen.user.service.member.MemberService;
import com.fleencorp.localizer.service.Localizer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static com.fleencorp.base.util.ExceptionUtil.checkIsNull;
import static com.fleencorp.base.util.FleenUtil.toSearchResult;
import static com.fleencorp.feen.service.impl.common.MiscServiceImpl.setEntityUpdatableByUser;
import static com.fleencorp.feen.util.CommonUtil.allNonNull;
import static java.util.Objects.nonNull;

/**
 * Implementation of the {@link ReviewService} interface for managing stream reviews.
 *
 * <p>This class provides methods to add, delete, and search for stream reviews, leveraging
 * repositories for data persistence and localization services for user-friendly responses.</p>
 *
 * <p>It encapsulates the business logic related to stream reviews and acts as a bridge
 * between the application and the underlying data access layer.</p>
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
@Slf4j
@Service
public class ReviewServiceImpl implements ReviewService {

  private final LikeService likeService;
  private final MemberService memberService;
  private final StreamService streamService;
  private final ReviewRepository reviewRepository;
  private final ReviewMapper reviewMapper;
  private final Localizer localizer;

  /**
   * Constructs a new {@code ReviewServiceImpl}, responsible for managing reviews and related interactions
   * such as likes on reviews within streams.
   *
   * @param likeService the service used to handle like operations on reviews (injected lazily to avoid circular dependencies)
   * @param memberService the service for managing members
   * @param streamService the service for retrieving and validating stream-related data associated with reviews
   * @param reviewRepository the repository for performing CRUD operations on review entities
   * @param reviewMapper the mapper for converting between review entities and their corresponding DTOs
   * @param localizer the utility for resolving localized text for responses and messages
   */
  public ReviewServiceImpl(
      @Lazy final LikeService likeService,
      @Lazy final MemberService memberService,
      final StreamService streamService,
      final ReviewRepository reviewRepository,
      final ReviewMapper reviewMapper,
      final Localizer localizer) {
    this.likeService = likeService;
    this.memberService = memberService;
    this.streamService = streamService;
    this.reviewRepository = reviewRepository;
    this.reviewMapper = reviewMapper;
    this.localizer = localizer;
  }

  /**
   * Retrieves a paginated list of public reviews for the specified stream or event.
   *
   * <p>This method performs a search for reviews associated with the given stream ID, based on the provided
   * {@link SearchRequest}. It converts the retrieved reviews into a list of {@link ReviewResponse}, checks if any
   * of the reviews are updatable by the current user, and includes pagination details in the result.</p>
   *
   * <p>If the user has authored any of the reviews, those reviews will be marked as updatable.</p>
   *
   * @param searchRequest the pagination and sorting information for the review search
   * @param user the {@link RegisteredUser} to check ownership of reviews and mark them as updatable if applicable
   * @return a {@link ReviewSearchResult} containing the reviews and pagination details, or an empty result if no reviews are found
   */
  @Override
  public ReviewSearchResult findReviews(final ReviewSearchRequest searchRequest, final RegisteredUser user) {
    final Long parentId = searchRequest.getParentId();
    final Pageable pageable = searchRequest.getPage();
    Page<Review> page = Page.empty();

    // Check if a stream review search request
    if (searchRequest.isStreamReviewSearchRequest()) {
      page = reviewRepository.findByStreamId(parentId, pageable);
    }

    // Convert and process the reviews to responses
    return processAndReturnReviews(page, user.toMember());
  }

  /**
   * Retrieves the most recent review for the specified stream or event.
   *
   * <p>This method looks up the most recent review associated with the given stream ID. If a review is found,
   * it is mapped to a {@link ReviewResponse} and returned. If no review is found, {@code null} is returned.
   * Additionally, if the review was created by the current user, it will be marked as updatable.</p>
   *
   * @param reviewParentType the type of reviews to find
   * @param parentId the ID of the parent which review is a child of
   * @param user the {@link RegisteredUser} whose ownership of the review is being verified for updatability
   * @return the {@link ReviewResponse} for the most recent review, or {@code null} if no review is found
   */
  @Override
  public ReviewResponse findMostRecentReview(final ReviewParentType reviewParentType, final Long parentId, final RegisteredUser user) {
    // Prepare search request details
    final PageRequest pageRequest = PageRequest.of(0, 1);
    // List of review
    List<Review> reviews = new ArrayList<>();

    if (ReviewParentType.isStream(reviewParentType)) {
      reviews = reviewRepository.findMostRecentReviewByStream(parentId, pageRequest);
    }

    // Return the most recent review
    final List<ReviewResponse> reviewResponses = getMostRecentReviews(reviews);
    // Process other details of the reviews
    processReviewsOtherDetails(reviewResponses, user.toMember());
    // Return the review
    return reviewResponses.isEmpty() ? null : reviewResponses.getFirst();
  }

  /**
   * Retrieves stream reviews submitted by a specific user.
   *
   * <p>This method fetches a paginated list of reviews associated with the user,
   * converting them into response views suitable for presentation.</p>
   *
   * @param searchRequest contains pagination and search parameters for retrieving the reviews
   * @param user the user whose reviews are being retrieved
   * @return a {@link ReviewSearchResult} containing the paginated list of reviews in response format
   */
  @Override
  public ReviewSearchResult findMyReviews(final SearchRequest searchRequest, final RegisteredUser user) {
    // Find all user reviews
    final Page<Review> page = reviewRepository.findByMember(user.toMember(), searchRequest.getPage());
    // Convert and process the reviews to responses
    return processAndReturnReviews(page, user.toMember());
  }

  /**
   * Processes a page of reviews and returns a localized search result.
   *
   * <p>This method converts each {@link Review} in the page to a {@link ReviewResponse}, processes
   * additional review details, wraps the responses in a {@link SearchResult}, and then returns
   * a localized {@link ReviewSearchResult}.</p>
   *
   * @param page the paginated list of reviews
   * @param member the current member viewing the reviews
   * @return a localized {@link ReviewSearchResult}, or an empty result if input is null
   */
  protected ReviewSearchResult processAndReturnReviews(final Page<Review> page, final Member member) {
    if (allNonNull(page)) {
      // Convert Review entities to responses
      final List<ReviewResponse> reviewResponses = reviewMapper.toReviewResponsesPublic(page.getContent());
      // Process other details of the reviews
      processReviewsOtherDetails(reviewResponses, member);
      // Create the search result
      final SearchResult searchResult = toSearchResult(reviewResponses, page);
      // Create the search result
      final ReviewSearchResult reviewSearchResult = ReviewSearchResult.of(searchResult);
      // Return a search result with the responses and pagination details
      return localizer.of(reviewSearchResult);
    }

    return ReviewSearchResult.empty();
  }

  /**
   * Adds a review for the specified event or stream.
   *
   * <p>This method retrieves the event or stream by its ID, throwing a {@link StreamNotFoundException}
   * if the stream does not exist. It then ensures the stream has started or completed before allowing
   * the review to be added. Finally, the review is created and saved using the provided data.</p>
   *
   * @param addReviewDto the data transfer object containing the details of the review to be added
   * @param user the current user adding the review, used to associate the review with a member
   * @return an {@link AddReviewResponse} indicating the result of the review addition
   * @throws StreamNotFoundException if no event or stream is found with the specified ID
   * @throws CannotAddReviewIfStreamHasNotStartedException if the stream has not yet started and cannot be reviewed
   */
  @Override
  @Transactional
  public AddReviewResponse addReview(final AddReviewDto addReviewDto, final RegisteredUser user)
      throws StreamNotFoundException, CannotAddReviewIfStreamHasNotStartedException {
    final Member member = memberService.findMember(user.getId());
    // Get the necessary details
    final ReviewOtherDetailsHolder reviewOtherDetailsHolder = retrieveReviewOtherDetailsHolder(addReviewDto);
    final String parentTitle = reviewOtherDetailsHolder.parentTitle();
    final ChatSpace chatSpace = reviewOtherDetailsHolder.chatSpace();
    final FleenStream stream = reviewOtherDetailsHolder.stream();

    // Convert the dto to the entity
    final Review review = addReviewDto.toReview(member, parentTitle, chatSpace, stream);
    // Save the new StreamReview to the repository
    reviewRepository.save(review);
    // Create the review response
    final ReviewResponse reviewResponse = reviewMapper.toReviewResponsePublic(review);
    // Set the review is-updatable check
    setEntityUpdatableByUser(reviewResponse, user.getId());
    // Get the response
    final AddReviewResponse addReviewResponse = AddReviewResponse.of(reviewResponse);
    // Return a localized response for the added review
    return localizer.of(addReviewResponse);
  }

  /**
   * Updates an existing review for the specified event or stream.
   *
   * <p>This method finds the review by its ID, the associated stream, and the member who added it.
   * If the review is not found, a {@link ReviewNotFoundException} is thrown. The review is then
   * updated with the provided details, and the changes are saved.</p>
   *
   * @param reviewId the ID of the review to be updated
   * @param updateReviewDto the data transfer object containing the updated review details
   * @param user the current user updating the review, used to verify the ownership of the review
   * @return an {@link UpdateReviewResponse} indicating the result of the review update
   * @throws ReviewNotFoundException if no review is found with the specified ID for the stream and member
   * @throws StreamNotFoundException if no stream is found with the specified stream ID
   */
  @Override
  @Transactional
  public UpdateReviewResponse updateReview(final Long reviewId, final UpdateReviewDto updateReviewDto, final RegisteredUser user)
    throws ReviewNotFoundException, StreamNotFoundException, CannotAddReviewIfStreamHasNotStartedException,
      FailedOperationException {
    final Member member = user.toMember();

    // Find the associated review
    if (updateReviewDto.isStreamParent()) {
      final Review review = reviewRepository.findByReviewIdAndStreamAndMember(reviewId, updateReviewDto.getParentId(), member)
        .orElseThrow(ReviewNotFoundException.of(reviewId));

      // Update the existing review with the new details
      review.update(updateReviewDto.getReview(), updateReviewDto.getRating());
      // Save the StreamReview to the repository
      reviewRepository.save(review);
      // Create the review response
      final ReviewResponse reviewResponse = reviewMapper.toReviewResponsePublic(review);
      // Process other details of the reviews
      processReviewsOtherDetails(List.of(reviewResponse), member);
      // Get the response
      final UpdateReviewResponse updateReviewResponse = UpdateReviewResponse.of(reviewResponse);
      // Return a localized response for the updated review
      return localizer.of(updateReviewResponse);
    }

    throw new ReviewNotFoundException(reviewId);
  }

  /**
   * Populates additional metadata for each review response such as update eligibility and like status.
   *
   * <p>Each {@link ReviewResponse} is checked to determine if it can be updated by the current member,
   * and also enriched with the member’s like information if applicable.</p>
   *
   * @param reviewResponses the list of review responses to process
   * @param member the current member interacting with the reviews
   */
  protected void processReviewsOtherDetails(final Collection<ReviewResponse> reviewResponses, final Member member) {
    if (allNonNull(reviewResponses, member)) {
      reviewResponses.stream()
        .filter(Objects::nonNull)
        .forEach(reviewResponse -> {
          // Set the review is-updatable check
          setEntityUpdatableByUser(reviewResponse, member.getMemberId());
      });

      // Set the like info by the user if any
      likeService.populateLikesForReviews(reviewResponses, member);
    }
  }

  /**
   * Retrieves the {@link ReviewOtherDetailsHolder} based on the given {@link AddReviewDto}.
   *
   * <p>This method first checks that the provided {@code addReviewDto} is not {@code null}. It then evaluates
   * whether the review is associated with a stream by calling {@link AddReviewDto#isStreamParent()}. If so,
   * it attempts to retrieve and validate the parent {@link FleenStream} using the provided parent ID and
   * returns the corresponding {@link ReviewOtherDetailsHolder}.</p>
   *
   * @param addReviewDto the DTO containing information about the review, including its parent type and ID
   * @return a {@link ReviewOtherDetailsHolder} containing additional details related to the review
   * @throws FailedOperationException if the {@code addReviewDto} is {@code null} or the review parent type is invalid
   */
  protected ReviewOtherDetailsHolder retrieveReviewOtherDetailsHolder(final AddReviewDto addReviewDto) throws FailedOperationException {
    // Ensure reviewParentType is not null
    checkIsNull(addReviewDto, FailedOperationException::new);

    // If the reviewParentType is a stream, retrieve the associated FleenStream
     if (addReviewDto.isStreamParent()) {
       return validateStreamParent(addReviewDto.getParentId());
     }

     throw FailedOperationException.of();
  }

  /**
   * Validates the given stream ID and retrieves the associated {@link FleenStream} to ensure it is eligible for reviews.
   *
   * <p>If the {@code streamId} is not {@code null}, this method fetches the corresponding {@link FleenStream} using
   * {@link StreamService#findStream(Long)}. It then checks whether the stream is eligible to receive a review
   * by calling {@link FleenStream#checkAddReviewEligibility()}. If valid, it returns a {@link ReviewOtherDetailsHolder}
   * containing the stream.</p>
   *
   * @param streamId the ID of the stream to validate
   * @return a {@link ReviewOtherDetailsHolder} containing the stream
   * @throws FailedOperationException if the {@code streamId} is {@code null}
   */
  protected ReviewOtherDetailsHolder validateStreamParent(final Long streamId) {
    if (nonNull(streamId)) {
      final FleenStream stream = streamService.findStream(streamId);
      stream.checkAddReviewEligibility();

      return ReviewOtherDetailsHolder.of(null, stream);
    }

    throw FailedOperationException.of();
  }

  /**
   * Deletes a stream review by its ID and the associated member.
   *
   * <p>This method attempts to delete a review with the specified ID, associated with the current user.
   * If the review is found and successfully deleted, a localized {@link DeleteReviewResponse} is returned.</p>
   *
   * @param reviewId the ID of the stream review to delete
   * @param user the current user attempting to delete the review, used to ensure only the user's review is deleted
   * @return a {@link DeleteReviewResponse} indicating the outcome of the deletion
   */
  @Override
  @Transactional
  public DeleteReviewResponse deleteReview(final Long reviewId, final RegisteredUser user) {
    // Delete the StreamReview associated with the given review ID and user
    reviewRepository.deleteByStreamReviewIdAndMember(reviewId, user.toMember());

    final DeleteReviewResponse deleteReviewResponse = DeleteReviewResponse.of();
    return localizer.of(deleteReviewResponse);
  }

  /**
   * Increments the like count of the review identified by the given ID.
   *
   * <p>This method delegates to the repository to atomically
   * increment the stored count, and returns the updated value.</p>
   *
   * @param reviewId the ID of the review to increment the like count for
   * @return the updated like count as a {@code Long}
   */
  @Override
  @Transactional
  public Long incrementLikeCount(final Long reviewId) {
    final int total = reviewRepository.incrementAndGetLikeCount(reviewId);
    return (long) total;
  }

  /**
   * Decrements the like count of the review identified by the given ID.
   *
   * <p>This method delegates the operation to the underlying
   * repository, which atomically decrements the stored count
   * and returns the updated total.</p>
   *
   * @param reviewId the ID of the review to decrement the like count for
   * @return the updated like count as a {@code Long}
   */
  @Override
  @Transactional
  public Long decrementLikeCount(final Long reviewId) {
    final int total = reviewRepository.decrementAndGetLikeCount(reviewId);
    return (long) total;
  }

  /**
   * Returns the most recent review from the provided list of reviews, if available,
   * by mapping it to a {@link ReviewResponse} using the {@code reviewMapper}.
   *
   * <p>If the input list is {@code null} or empty, an empty list is returned.</p>
   *
   * @param reviews the list of {@link Review} objects to process
   * @return a list containing a single {@link ReviewResponse} representing the most recent review,
   *         or an empty list if the input is {@code null} or contains no elements
   */
  protected List<ReviewResponse> getMostRecentReviews(final List<Review> reviews) {
    if (nonNull(reviews)) {
      return reviews.stream()
        .findFirst()
        .map(reviewMapper::toReviewResponsePublic)
        .stream()
        .toList();
    }

    return List.of();
  }
}