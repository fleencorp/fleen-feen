package com.fleencorp.feen.service.impl.stream.review;

import com.fleencorp.base.model.request.search.SearchRequest;
import com.fleencorp.base.service.i18n.LocalizedResponse;
import com.fleencorp.feen.exception.stream.FleenStreamNotFoundException;
import com.fleencorp.feen.mapper.stream.review.StreamReviewMapper;
import com.fleencorp.feen.model.domain.stream.FleenStream;
import com.fleencorp.feen.model.domain.stream.StreamReview;
import com.fleencorp.feen.model.dto.stream.review.AddStreamReviewDto;
import com.fleencorp.feen.model.response.stream.review.AddStreamReviewResponse;
import com.fleencorp.feen.model.response.stream.review.DeleteStreamReviewResponse;
import com.fleencorp.feen.model.response.stream.review.StreamReviewResponse;
import com.fleencorp.feen.model.search.stream.review.EmptyStreamReviewSearchResult;
import com.fleencorp.feen.model.search.stream.review.StreamReviewSearchResult;
import com.fleencorp.feen.model.security.FleenUser;
import com.fleencorp.feen.repository.stream.FleenStreamRepository;
import com.fleencorp.feen.repository.stream.StreamReviewRepository;
import com.fleencorp.feen.service.stream.review.StreamReviewService;
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

  private final FleenStreamRepository streamRepository;
  private final StreamReviewRepository streamReviewRepository;
  private final StreamReviewMapper streamReviewMapper;
  private final LocalizedResponse localizedResponse;

  /**
   * Constructs a {@link StreamReviewServiceImpl} instance with the specified dependencies.
   *
   * <p>This constructor initializes the service with repositories for managing streams and reviews,
   * as well as a localized response handler for returning user-friendly messages.</p>
   *
   * @param streamRepository the repository for accessing stream data
   * @param streamReviewRepository the repository for accessing stream review data
   * @param localizedResponse the service for generating localized responses
   */
  public StreamReviewServiceImpl(
      final FleenStreamRepository streamRepository,
      final StreamReviewRepository streamReviewRepository,
      final StreamReviewMapper streamReviewMapper,
      final LocalizedResponse localizedResponse) {
    this.streamRepository = streamRepository;
    this.streamReviewRepository = streamReviewRepository;
    this.streamReviewMapper = streamReviewMapper;
    this.localizedResponse = localizedResponse;
  }

  /**
   * Finds and retrieves stream reviews for a given event or stream.
   *
   * <p>This method retrieves a paginated list of reviews associated with the specified event or stream ID
   * and converts them into response views for presentation.</p>
   *
   * @param eventOrStreamId the ID of the event or stream whose reviews are being retrieved
   * @param searchRequest contains pagination and other search parameters for fetching the reviews
   * @return a {@link StreamReviewSearchResult} containing the paginated list of reviews in response format
   */
  @Override
  public StreamReviewSearchResult findReviews(final Long eventOrStreamId, final SearchRequest searchRequest) {
    final Page<StreamReview> page = streamReviewRepository.findByStream(FleenStream.of(eventOrStreamId), searchRequest.getPage());
    final List<StreamReviewResponse> views = streamReviewMapper.toStreamReviewResponsesMore(page.getContent());

    // Return a search result view with the review responses and pagination details
    return handleSearchResult(
      page,
      localizedResponse.of(StreamReviewSearchResult.of(toSearchResult(views, page))),
      localizedResponse.of(EmptyStreamReviewSearchResult.of(toSearchResult(List.of(), page)))
    );
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
      localizedResponse.of(StreamReviewSearchResult.of(toSearchResult(views, page))),
      localizedResponse.of(EmptyStreamReviewSearchResult.of(toSearchResult(List.of(), page)))
    );
  }

  /**
   * Adds a review for the specified event or stream.
   *
   * <p>This method retrieves the event or stream by its ID, throws a {@link FleenStreamNotFoundException} if not found,
   * and then creates and saves a new review using the provided data.</p>
   *
   * @param eventOrStreamId the ID of the event or stream to which the review is being added
   * @param addStreamReviewDto the data transfer object containing review details
   * @param user the current user adding the review, used to associate the review with a member
   * @return an {@link AddStreamReviewResponse} indicating the outcome of the review addition
   * @throws FleenStreamNotFoundException if no event or stream is found with the provided ID
   */
  @Override
  @Transactional
  public AddStreamReviewResponse addReview(final Long eventOrStreamId, final AddStreamReviewDto addStreamReviewDto, final FleenUser user) {
    // Check if the event or stream exists; throw exception if not
    streamRepository.findById(eventOrStreamId)
      .orElseThrow(() -> new FleenStreamNotFoundException(eventOrStreamId));

    final StreamReview streamReview = addStreamReviewDto
      .toStreamReview(FleenStream.of(eventOrStreamId), user.toMember());

    // Save the new StreamReview to the repository
    streamReviewRepository.save(streamReview);
    // Return a localized response for the added review
    return localizedResponse.of(AddStreamReviewResponse.of());
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
    return localizedResponse.of(DeleteStreamReviewResponse.of());
  }
}
