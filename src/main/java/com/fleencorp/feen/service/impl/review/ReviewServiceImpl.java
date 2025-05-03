package com.fleencorp.feen.service.impl.review;

import com.fleencorp.base.model.request.search.SearchRequest;
import com.fleencorp.base.model.view.search.SearchResultView;
import com.fleencorp.feen.constant.review.ReviewType;
import com.fleencorp.feen.exception.base.FailedOperationException;
import com.fleencorp.feen.exception.review.CannotAddReviewIfStreamHasNotStartedException;
import com.fleencorp.feen.exception.review.ReviewNotFoundException;
import com.fleencorp.feen.exception.stream.StreamNotFoundException;
import com.fleencorp.feen.mapper.review.ReviewMapper;
import com.fleencorp.feen.model.domain.review.Review;
import com.fleencorp.feen.model.domain.stream.FleenStream;
import com.fleencorp.feen.model.domain.user.Member;
import com.fleencorp.feen.model.dto.stream.review.AddReviewDto;
import com.fleencorp.feen.model.dto.stream.review.UpdateReviewDto;
import com.fleencorp.feen.model.holder.ReviewOtherDetailsHolder;
import com.fleencorp.feen.model.request.search.review.ReviewSearchRequest;
import com.fleencorp.feen.model.response.review.AddReviewResponse;
import com.fleencorp.feen.model.response.review.DeleteReviewResponse;
import com.fleencorp.feen.model.response.review.ReviewResponse;
import com.fleencorp.feen.model.response.review.UpdateReviewResponse;
import com.fleencorp.feen.model.search.review.ReviewSearchResult;
import com.fleencorp.feen.model.security.FleenUser;
import com.fleencorp.feen.repository.review.ReviewRepository;
import com.fleencorp.feen.service.review.ReviewService;
import com.fleencorp.feen.service.stream.common.StreamService;
import com.fleencorp.localizer.service.Localizer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.fleencorp.base.util.ExceptionUtil.checkIsNull;
import static com.fleencorp.base.util.FleenUtil.toSearchResult;
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

  private final StreamService streamService;
  private final ReviewRepository reviewRepository;
  private final ReviewMapper reviewMapper;
  private final Localizer localizer;

  /**
   * Constructs a {@link ReviewServiceImpl} instance with the specified dependencies.
   *
   * <p>This constructor initializes the service with repositories for managing streams and reviews,
   * as well as a localized response handler for returning user-friendly messages.</p>
   *
   * @param streamService the service for accessing and managing streams
   * @param reviewRepository the repository for accessing stream review data
   * @param localizer the service for generating localized responses
   */
  public ReviewServiceImpl(
    final StreamService streamService,
    final ReviewRepository reviewRepository,
    final ReviewMapper reviewMapper,
    final Localizer localizer) {
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
   * @param user the {@link FleenUser} to check ownership of reviews and mark them as updatable if applicable
   * @return a {@link ReviewSearchResult} containing the reviews and pagination details, or an empty result if no reviews are found
   */
  @Override
  public ReviewSearchResult findReviews(final ReviewSearchRequest searchRequest, final FleenUser user) {
    Page<Review> page = Page.empty();

    // Check if a stream review search request
    if (searchRequest.isStreamReviewSearchRequest()) {
      page = reviewRepository.findByStreamId(searchRequest.getParentId(), searchRequest.getPage());
    }

    // Convert the reviews to the response
    final List<ReviewResponse> reviewResponses = reviewMapper.toReviewResponsesPublic(page.getContent());
    // Check and set the reviews that are updatable by the user
    setReviewsThatAreUpdatableByUser(reviewResponses, user.getId());

    // Create the search result view
    final SearchResultView searchResultView = toSearchResult(reviewResponses, page);
    // Create the search result
    final ReviewSearchResult searchResult = ReviewSearchResult.of(searchResultView);
    // Return a search result view with the review responses and pagination details
    return localizer.of(searchResult);
  }

  /**
   * Retrieves the most recent review for the specified stream or event.
   *
   * <p>This method looks up the most recent review associated with the given stream ID. If a review is found,
   * it is mapped to a {@link ReviewResponse} and returned. If no review is found, {@code null} is returned.
   * Additionally, if the review was created by the current user, it will be marked as updatable.</p>
   *
   * @param reviewType the type of reviews to find
   * @param parentId the ID of the parent which review is a child of
   * @param user the {@link FleenUser} whose ownership of the review is being verified for updatability
   * @return the {@link ReviewResponse} for the most recent review, or {@code null} if no review is found
   */
  @Override
  public ReviewResponse findMostRecentReview(final ReviewType reviewType, final Long parentId, final FleenUser user) {
    // Prepare search request details
    final PageRequest pageRequest = PageRequest.of(0, 1);
    // List of review
    List<Review> reviews = new ArrayList<>();

    if (ReviewType.isStream(reviewType)) {
      reviews = findMostRecentReviewByStream(parentId, pageRequest);
    }

    // Return the most recent review
    return extractMostRecentReview(reviews, user.getId());
  }

  /**
   * Retrieves the most recent review(s) for a specified stream using pagination.
   *
   * <p>This method queries the review repository to find the most recent review(s) associated
   * with the given stream ID, based on the provided {@link PageRequest}. Pagination is used to
   * limit the number of reviews returned.</p>
   *
   * @param streamId the ID of the stream whose most recent review(s) are being retrieved
   * @param pageRequest the {@link PageRequest} object specifying pagination details (page number, size, etc.)
   * @return a list of {@link Review} objects for the most recent review(s) of the stream
   */
  private List<Review> findMostRecentReviewByStream(final Long streamId, final PageRequest pageRequest) {
    return reviewRepository.findMostRecentReviewByStream(streamId, pageRequest);
  }

  /**
   * Extracts the most recent review from the provided list of reviews and marks it as updatable if applicable.
   *
   * <p>This method processes a list of {@link Review} objects, retrieves the most recent one (if any), and
   * converts it to a {@link ReviewResponse}. If the review is authored by the specified user, it is marked
   * as updatable.</p>
   *
   * @param reviews the list of reviews from which the most recent review is extracted
   * @param userId the ID of the user to check if they authored the review and mark it as updatable
   * @return the {@link ReviewResponse} of the most recent review, or {@code null} if no review is found
   */
  private ReviewResponse extractMostRecentReview(final List<Review> reviews, final Long userId) {
    if (nonNull(reviews)) {
      return reviews.stream()
        .findFirst()
        .map(reviewMapper::toReviewResponsePublic)
        .map(reviewResponse -> {
          // Mark the review as updatable if necessary
          setReviewAsUpdatableIfApplicable(reviewResponse, userId);
          return reviewResponse;
        })
        .orElse(null);
    }

    return null;
  }

  /**
   * Marks reviews that are updatable by the given member.
   *
   * <p>This method iterates through a list of {@link ReviewResponse} objects and marks reviews as
   * updatable if they were created by the specified {@link Member}. A review is considered updatable
   * if the member who created it matches the member passed to this method.</p>
   *
   * @param reviews the list of {@link ReviewResponse} objects to check for update eligibility
   * @param userId the user whose reviews should be marked as updatable
   */
  protected static void setReviewsThatAreUpdatableByUser(final List<ReviewResponse> reviews, final Long userId) {
    if (nonNull(reviews) && nonNull(userId)) {
      reviews.forEach(review -> {
        if (nonNull(review.getMemberId()) && review.getMemberId().equals(userId)) {
          review.markAsUpdatable();
        }
      });
    }
  }

  /**
   * Marks the given review as updatable if it belongs to the specified member.
   *
   * <p>This method checks if the provided {@link ReviewResponse} was created by the given {@link Member}.
   * If so, it marks the review as updatable. This is a helper method that wraps the logic of marking a
   * single review, utilizing {@link #setReviewsThatAreUpdatableByUser(List, Long)}.</p>
   *
   * @param reviewResponse the {@link ReviewResponse} to be checked and potentially marked as updatable
   * @param userId the user whose ownership of the review is being verified
   */
  protected static void setReviewAsUpdatableIfApplicable(final ReviewResponse reviewResponse, final Long userId) {
    if (nonNull(reviewResponse)) {
      final List<ReviewResponse> reviewResponses = Collections.singletonList(reviewResponse);
      setReviewsThatAreUpdatableByUser(reviewResponses, userId);
    }
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
  public ReviewSearchResult findMyReviews(final SearchRequest searchRequest, final FleenUser user) {
    // Find all user reviews
    final Page<Review> page = reviewRepository.findByMember(user.toMember(), searchRequest.getPage());
    // Convert the reviews to the response
    final List<ReviewResponse> reviewResponses = reviewMapper.toReviewResponsesPrivate(page.getContent());
    // Create the search result view
    final SearchResultView searchResultView = toSearchResult(reviewResponses, page);
    // Create the search result
    final ReviewSearchResult searchResult = ReviewSearchResult.of(searchResultView);
    // Return a search result view with the review responses and pagination details
    return localizer.of(searchResult);
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
  public AddReviewResponse addReview(final AddReviewDto addReviewDto, final FleenUser user)
      throws StreamNotFoundException, CannotAddReviewIfStreamHasNotStartedException {
    // Get the review type
    final ReviewType reviewType = addReviewDto.getReviewType();
    // Get the necessary details
    final ReviewOtherDetailsHolder reviewOtherDetailsHolder = retrieveReviewOtherDetailsHolder(reviewType, addReviewDto.getParentId());
    // Retrieve the stream
    final FleenStream stream = reviewOtherDetailsHolder.stream();
    // Check other details
    checkAddReviewEligibility(reviewType, stream);
    // Convert the dto to the entity
    final Review review = addReviewDto.toStreamReview(stream, user.toMember());

    // Save the new StreamReview to the repository
    reviewRepository.save(review);

    // Create the review response
    final ReviewResponse reviewResponse = reviewMapper.toReviewResponsePublic(review);
    // Set the review is-updatable check
    setReviewAsUpdatableIfApplicable(reviewResponse, user.getId());
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
  public UpdateReviewResponse updateReview(final Long reviewId, final UpdateReviewDto updateReviewDto, final FleenUser user)
    throws ReviewNotFoundException, StreamNotFoundException, CannotAddReviewIfStreamHasNotStartedException,
      FailedOperationException {
    // Find the associated review
    if (updateReviewDto.isStreamReviewType()) {
      final Review review = reviewRepository.findByReviewIdAndStreamAndMember(reviewId, updateReviewDto.getParentId(), user.toMember())
        .orElseThrow(ReviewNotFoundException.of(reviewId));

      // Update the existing review with the new details
      review.update(updateReviewDto.getReview(), updateReviewDto.getRating());

      // Save the StreamReview to the repository
      reviewRepository.save(review);

      // Create the review response
      final ReviewResponse reviewResponse = reviewMapper.toReviewResponsePublic(review);
      // Set the review is-updatable check
      setReviewAsUpdatableIfApplicable(reviewResponse, user.getId());
      // Get the response
      final UpdateReviewResponse updateReviewResponse = UpdateReviewResponse.of(reviewResponse);
      // Return a localized response for the updated review
      return localizer.of(updateReviewResponse);
    }

    throw new ReviewNotFoundException(reviewId);
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
  public DeleteReviewResponse deleteReview(final Long reviewId, final FleenUser user) {
    // Delete the StreamReview associated with the given review ID and user
    reviewRepository.deleteByStreamReviewIdAndMember(reviewId, user.toMember());
    // Return the response
    return localizer.of(DeleteReviewResponse.of());
  }

  /**
   * Retrieves the {@link ReviewOtherDetailsHolder} for the specified review type and stream ID.
   *
   * <p>This method checks the validity of the {@code reviewType} parameter and retrieves the corresponding
   * {@link FleenStream} if the review type is a stream. The {@link ReviewOtherDetailsHolder} is then created
   * based on the stream details.</p>
   *
   * @param reviewType the type of review, used to determine whether to retrieve stream details
   * @param streamId the ID of the stream, applicable only if the review type is {@code STREAM}
   * @return a {@link ReviewOtherDetailsHolder} containing other details related to the review
   * @throws FailedOperationException if the {@code reviewType} is null or invalid
   */
  protected ReviewOtherDetailsHolder retrieveReviewOtherDetailsHolder(final ReviewType reviewType, final Long streamId) throws FailedOperationException {
    // Ensure reviewType is not null
    checkIsNull(reviewType, FailedOperationException::new);

    // If the reviewType is a stream, retrieve the associated FleenStream
    final FleenStream stream = ReviewType.isStream(reviewType)
      ? streamService.findStream(streamId)
      : null;

    // Return the ReviewOtherDetailsHolder based on the stream
    return ReviewOtherDetailsHolder.of(stream);
  }

  /**
   * Checks the eligibility for adding a review based on the review type and the stream status.
   *
   * <p>This method ensures that a review can only be added for streams that are ongoing or completed.
   * If the stream has not started yet, it throws a {@link CannotAddReviewIfStreamHasNotStartedException}.</p>
   *
   * @param reviewType the type of the review, used to verify the type of review being added
   * @param stream the {@link FleenStream} being reviewed, used to determine the stream's current status
   * @throws CannotAddReviewIfStreamHasNotStartedException if the stream has not started yet
   */
  protected void checkAddReviewEligibility(final ReviewType reviewType, final FleenStream stream) {
    // Only streams that are ongoing or completed can be reviewed
    if (nonNull(reviewType) && nonNull(stream) && stream.hasNotStarted()) {
      throw CannotAddReviewIfStreamHasNotStartedException.of();
    }
  }
}