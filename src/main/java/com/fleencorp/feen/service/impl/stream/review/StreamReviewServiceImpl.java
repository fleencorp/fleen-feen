package com.fleencorp.feen.service.impl.stream.review;

import com.fleencorp.base.model.request.search.SearchRequest;
import com.fleencorp.feen.exception.stream.FleenStreamNotFoundException;
import com.fleencorp.feen.exception.stream.review.CannotAddReviewIfStreamHasNotStartedException;
import com.fleencorp.feen.exception.stream.review.ReviewNotFoundException;
import com.fleencorp.feen.mapper.stream.review.StreamReviewMapper;
import com.fleencorp.feen.model.domain.stream.FleenStream;
import com.fleencorp.feen.model.domain.stream.StreamReview;
import com.fleencorp.feen.model.dto.stream.review.AddStreamReviewDto;
import com.fleencorp.feen.model.dto.stream.review.UpdateStreamReviewDto;
import com.fleencorp.feen.model.response.stream.review.AddStreamReviewResponse;
import com.fleencorp.feen.model.response.stream.review.DeleteStreamReviewResponse;
import com.fleencorp.feen.model.response.stream.review.StreamReviewResponse;
import com.fleencorp.feen.model.response.stream.review.UpdateStreamReviewResponse;
import com.fleencorp.feen.model.search.stream.review.EmptyStreamReviewSearchResult;
import com.fleencorp.feen.model.search.stream.review.StreamReviewSearchResult;
import com.fleencorp.feen.model.security.FleenUser;
import com.fleencorp.feen.repository.stream.StreamReviewRepository;
import com.fleencorp.feen.service.stream.common.StreamService;
import com.fleencorp.feen.service.stream.review.StreamReviewService;
import com.fleencorp.localizer.service.Localizer;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.fleencorp.base.util.FleenUtil.handleSearchResult;
import static com.fleencorp.base.util.FleenUtil.toSearchResult;

/**
 * Implementation of the {@link StreamReviewService} interface for managing stream reviews.
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
public class StreamReviewServiceImpl implements StreamReviewService {

  private final StreamService streamService;
  private final StreamReviewRepository streamReviewRepository;
  private final StreamReviewMapper streamReviewMapper;
  private final Localizer localizer;

  /**
   * Constructs a {@link StreamReviewServiceImpl} instance with the specified dependencies.
   *
   * <p>This constructor initializes the service with repositories for managing streams and reviews,
   * as well as a localized response handler for returning user-friendly messages.</p>
   *
   * @param streamService the service for accessing and managing streams
   * @param streamReviewRepository the repository for accessing stream review data
   * @param localizer the service for generating localized responses
   */
  public StreamReviewServiceImpl(
      final StreamService streamService,
      final StreamReviewRepository streamReviewRepository,
      final StreamReviewMapper streamReviewMapper,
      final Localizer localizer) {
    this.streamService = streamService;
    this.streamReviewRepository = streamReviewRepository;
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
   * @return a {@link StreamReviewSearchResult} containing the paginated list of reviews in response format
   */
  @Override
  public StreamReviewSearchResult findReviews(final Long streamId, final SearchRequest searchRequest) {
    final Page<StreamReview> page = streamReviewRepository.findByStream(FleenStream.of(streamId), searchRequest.getPage());
    final List<StreamReviewResponse> views = streamReviewMapper.toStreamReviewResponsesMore(page.getContent());

    // Return a search result view with the review responses and pagination details
    return handleSearchResult(
      page,
      localizer.of(StreamReviewSearchResult.of(toSearchResult(views, page))),
      localizer.of(EmptyStreamReviewSearchResult.of(toSearchResult(List.of(), page)))
    );
  }

  /**
   * Retrieves the most recent review for the specified stream or event.
   *
   * <p>This method looks up the review associated with the given stream ID. If a review is found, it is
   * mapped to a {@link StreamReviewResponse} and returned. If no review is found, {@code null} is returned.</p>
   *
   * @param streamId the ID of the stream or event whose most recent review is being retrieved
   * @return the {@link StreamReviewResponse} for the most recent review, or {@code null} if no review is found
   */
  @Override
  public StreamReviewResponse findMostRecentReview(final Long streamId) {
    return streamReviewRepository.findMostRecentReviewByStream(FleenStream.of(streamId))
      .map(streamReviewMapper::toStreamReviewResponse)
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
   * @return a {@link StreamReviewSearchResult} containing the paginated list of reviews in response format
   */
  @Override
  public StreamReviewSearchResult findReviews(final SearchRequest searchRequest, final FleenUser user) {
    final Page<StreamReview> page = streamReviewRepository.findByMember(user.toMember(), searchRequest.getPage());
    final List<StreamReviewResponse> views = streamReviewMapper.toStreamReviewResponses(page.getContent());

    // Return a search result view with the review responses and pagination details
    return handleSearchResult(
      page,
      localizer.of(StreamReviewSearchResult.of(toSearchResult(views, page))),
      localizer.of(EmptyStreamReviewSearchResult.of(toSearchResult(List.of(), page)))
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
   * @param addStreamReviewDto the data transfer object containing the details of the review to be added
   * @param user the current user adding the review, used to associate the review with a member
   * @return an {@link AddStreamReviewResponse} indicating the result of the review addition
   * @throws FleenStreamNotFoundException if no event or stream is found with the specified ID
   * @throws CannotAddReviewIfStreamHasNotStartedException if the stream has not yet started and cannot be reviewed
   */
  @Override
  @Transactional
  public AddStreamReviewResponse addReview(final Long streamId, final AddStreamReviewDto addStreamReviewDto, final FleenUser user)
      throws FleenStreamNotFoundException, CannotAddReviewIfStreamHasNotStartedException {
    // Retrieve the stream
    final FleenStream stream = streamService.findStream(streamId);
    // Only streams that are ongoing or completed can be reviewed
    if (stream.hasNotStarted()) {
      throw CannotAddReviewIfStreamHasNotStartedException.of();
    }

    final StreamReview streamReview = addStreamReviewDto
      .toStreamReview(FleenStream.of(streamId), user.toMember());

    // Save the new StreamReview to the repository
    streamReviewRepository.save(streamReview);
    // Return a localized response for the added review
    return localizer.of(AddStreamReviewResponse.of());
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
   * @return an {@link UpdateStreamReviewResponse} indicating the result of the review update
   * @throws ReviewNotFoundException if no review is found with the specified ID for the stream and member
   * @throws FleenStreamNotFoundException if no stream is found with the specified stream ID
   */
  @Override
  @Transactional
  public UpdateStreamReviewResponse updateReview(final Long streamId, final Long reviewId, final UpdateStreamReviewDto updateStreamReviewDto, final FleenUser user)
      throws ReviewNotFoundException, FleenStreamNotFoundException, CannotAddReviewIfStreamHasNotStartedException {
    // Find the associated review
    final StreamReview streamReview = streamReviewRepository.findByReviewIdAndStreamAndMember(reviewId, FleenStream.of(streamId), user.toMember())
      .orElseThrow(ReviewNotFoundException.of(reviewId));

    // Update the existing review with the new details
    streamReview.update(updateStreamReviewDto.getReview(), updateStreamReviewDto.getRating());

    // Save the StreamReview to the repository
    streamReviewRepository.save(streamReview);
    // Return a localized response for the updated review
    return localizer.of(UpdateStreamReviewResponse.of());
  }

  /**
   * Deletes a stream review by its ID and the associated member.
   *
   * <p>This method attempts to delete a review with the specified ID, associated with the current user.
   * If the review is found and successfully deleted, a localized {@link DeleteStreamReviewResponse} is returned.</p>
   *
   * @param reviewId the ID of the stream review to delete
   * @param user the current user attempting to delete the review, used to ensure only the user's review is deleted
   * @return a {@link DeleteStreamReviewResponse} indicating the outcome of the deletion
   */
  @Override
  @Transactional
  public DeleteStreamReviewResponse deleteReview(final Long reviewId, final FleenUser user) {
    // Delete the StreamReview associated with the given review ID and user
    streamReviewRepository.deleteByStreamReviewIdAndMember(reviewId, user.toMember());
    return localizer.of(DeleteStreamReviewResponse.of());
  }
}
