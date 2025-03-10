package com.fleencorp.feen.service.impl.review;

import com.fleencorp.base.model.request.search.SearchRequest;
import com.fleencorp.feen.exception.review.CannotAddReviewIfStreamHasNotStartedException;
import com.fleencorp.feen.exception.review.ReviewNotFoundException;
import com.fleencorp.feen.exception.stream.FleenStreamNotFoundException;
import com.fleencorp.feen.mapper.stream.review.StreamReviewMapper;
import com.fleencorp.feen.model.domain.review.Review;
import com.fleencorp.feen.model.domain.stream.FleenStream;
import com.fleencorp.feen.model.dto.stream.review.AddReviewDto;
import com.fleencorp.feen.model.dto.stream.review.UpdateReviewDto;
import com.fleencorp.feen.model.response.review.AddReviewResponse;
import com.fleencorp.feen.model.response.review.DeleteReviewResponse;
import com.fleencorp.feen.model.response.review.ReviewResponse;
import com.fleencorp.feen.model.response.review.UpdateReviewResponse;
import com.fleencorp.feen.model.search.review.EmptyReviewSearchResult;
import com.fleencorp.feen.model.search.review.ReviewSearchResult;
import com.fleencorp.feen.model.security.FleenUser;
import com.fleencorp.feen.repository.review.ReviewRepository;
import com.fleencorp.feen.service.review.ReviewService;
import com.fleencorp.feen.service.stream.common.StreamService;
import com.fleencorp.localizer.service.Localizer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.fleencorp.base.util.FleenUtil.handleSearchResult;
import static com.fleencorp.base.util.FleenUtil.toSearchResult;

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
@Service
public class ReviewServiceImpl implements ReviewService {

  private final StreamService streamService;
  private final ReviewRepository reviewRepository;
  private final StreamReviewMapper streamReviewMapper;
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
    final StreamReviewMapper streamReviewMapper,
    final Localizer localizer) {
    this.streamService = streamService;
    this.reviewRepository = reviewRepository;
    this.streamReviewMapper = streamReviewMapper;
    this.localizer = localizer;
  }

  /**
   * Finds and retrieves stream reviews for a given event or stream.
   *
   * <p>This method retrieves a paginated list of reviews associated with the specified event or stream ID
   * and converts them into response views for presentation.</p>
   *
   * @param streamId the ID of the event or stream whose reviews are being retrieved
   * @param searchRequest contains pagination and other search parameters for fetching the reviews
   * @return a {@link ReviewSearchResult} containing the paginated list of reviews in response format
   */
  @Override
  public ReviewSearchResult findReviewsPublic(final Long streamId, final SearchRequest searchRequest) {
    final Page<Review> page = reviewRepository.findByStream(FleenStream.of(streamId), searchRequest.getPage());
    // Convert the reviews to the response
    final List<ReviewResponse> views = streamReviewMapper.toStreamReviewResponsesPublic(page.getContent());

    // Return a search result view with the review responses and pagination details
    return handleSearchResult(
      page,
      localizer.of(ReviewSearchResult.of(toSearchResult(views, page))),
      localizer.of(EmptyReviewSearchResult.of(toSearchResult(List.of(), page)))
    );
  }

  /**
   * Retrieves the most recent review for the specified stream or event.
   *
   * <p>This method looks up the review associated with the given stream ID. If a review is found, it is
   * mapped to a {@link ReviewResponse} and returned. If no review is found, {@code null} is returned.</p>
   *
   * @param streamId the ID of the stream or event whose most recent review is being retrieved
   * @return the {@link ReviewResponse} for the most recent review, or {@code null} if no review is found
   */
  @Override
  public ReviewResponse findMostRecentReview(final Long streamId) {
    // Prepare search request details
    final FleenStream stream = FleenStream.of(streamId);
    final PageRequest pageRequest = PageRequest.of(0, 1);

    // Return the most recent review
    return reviewRepository.findMostRecentReviewByStream(stream, pageRequest)
      .stream()
      .findFirst()
      .map(streamReviewMapper::toStreamReviewResponsePublic)
      .orElse(null);
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
  public ReviewSearchResult findReviewsPrivate(final SearchRequest searchRequest, final FleenUser user) {
    // Find all user reviews
    final Page<Review> page = reviewRepository.findByMember(user.toMember(), searchRequest.getPage());
    // Convert the reviews to the response
    final List<ReviewResponse> views = streamReviewMapper.toStreamReviewResponsesPrivate(page.getContent());

    // Return a search result view with the review responses and pagination details
    return handleSearchResult(
      page,
      localizer.of(ReviewSearchResult.of(toSearchResult(views, page))),
      localizer.of(EmptyReviewSearchResult.of(toSearchResult(List.of(), page)))
    );
  }

  /**
   * Adds a review for the specified event or stream.
   *
   * <p>This method retrieves the event or stream by its ID, throwing a {@link FleenStreamNotFoundException}
   * if the stream does not exist. It then ensures the stream has started or completed before allowing
   * the review to be added. Finally, the review is created and saved using the provided data.</p>
   *
   * @param streamId the ID of the event or stream to which the review is being added
   * @param addReviewDto the data transfer object containing the details of the review to be added
   * @param user the current user adding the review, used to associate the review with a member
   * @return an {@link AddReviewResponse} indicating the result of the review addition
   * @throws FleenStreamNotFoundException if no event or stream is found with the specified ID
   * @throws CannotAddReviewIfStreamHasNotStartedException if the stream has not yet started and cannot be reviewed
   */
  @Override
  @Transactional
  public AddReviewResponse addReview(final Long streamId, final AddReviewDto addReviewDto, final FleenUser user)
    throws FleenStreamNotFoundException, CannotAddReviewIfStreamHasNotStartedException {
    // Retrieve the stream
    final FleenStream stream = streamService.findStream(streamId);
    // Only streams that are ongoing or completed can be reviewed
    if (stream.hasNotStarted()) {
      throw CannotAddReviewIfStreamHasNotStartedException.of();
    }

    // Convert the dto to the entity
    final Review review = addReviewDto.toStreamReview(stream, user.toMember());
    // Set the stream title of the review
    review.setStreamTitle(stream.getTitle());

    // Save the new StreamReview to the repository
    reviewRepository.save(review);
    // Return a localized response for the added review
    return localizer.of(AddReviewResponse.of());
  }

  /**
   * Updates an existing review for the specified event or stream.
   *
   * <p>This method finds the review by its ID, the associated stream, and the member who added it.
   * If the review is not found, a {@link ReviewNotFoundException} is thrown. The review is then
   * updated with the provided details, and the changes are saved.</p>
   *
   * @param streamId the ID of the event or stream to which the review belongs
   * @param reviewId the ID of the review to be updated
   * @param updateStreamReviewDto the data transfer object containing the updated review details
   * @param user the current user updating the review, used to verify the ownership of the review
   * @return an {@link UpdateReviewResponse} indicating the result of the review update
   * @throws ReviewNotFoundException if no review is found with the specified ID for the stream and member
   * @throws FleenStreamNotFoundException if no stream is found with the specified stream ID
   */
  @Override
  @Transactional
  public UpdateReviewResponse updateReview(final Long streamId, final Long reviewId, final UpdateReviewDto updateStreamReviewDto, final FleenUser user)
    throws ReviewNotFoundException, FleenStreamNotFoundException, CannotAddReviewIfStreamHasNotStartedException {
    // Find the associated review
    final Review review = reviewRepository.findByReviewIdAndStreamAndMember(reviewId, FleenStream.of(streamId), user.toMember())
      .orElseThrow(ReviewNotFoundException.of(reviewId));

    // Update the existing review with the new details
    review.update(updateStreamReviewDto.getReview(), updateStreamReviewDto.getRating());

    // Save the StreamReview to the repository
    reviewRepository.save(review);
    // Return a localized response for the updated review
    return localizer.of(UpdateReviewResponse.of());
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
}