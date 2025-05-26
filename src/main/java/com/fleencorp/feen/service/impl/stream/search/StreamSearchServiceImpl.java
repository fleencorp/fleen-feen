package com.fleencorp.feen.service.impl.stream.search;

import com.fleencorp.base.model.view.search.SearchResult;
import com.fleencorp.feen.aspect.MeasureExecutionTime;
import com.fleencorp.feen.constant.review.ReviewParentType;
import com.fleencorp.feen.constant.stream.StreamStatus;
import com.fleencorp.feen.constant.stream.StreamTimeType;
import com.fleencorp.feen.constant.stream.StreamType;
import com.fleencorp.feen.constant.stream.StreamVisibility;
import com.fleencorp.feen.exception.stream.StreamNotFoundException;
import com.fleencorp.feen.mapper.stream.StreamMapper;
import com.fleencorp.feen.model.domain.stream.FleenStream;
import com.fleencorp.feen.model.domain.user.Member;
import com.fleencorp.feen.model.info.stream.StreamTypeInfo;
import com.fleencorp.feen.model.request.search.stream.StreamSearchRequest;
import com.fleencorp.feen.model.request.search.stream.type.StreamTypeSearchRequest;
import com.fleencorp.feen.model.response.review.ReviewResponse;
import com.fleencorp.feen.model.response.stream.StreamResponse;
import com.fleencorp.feen.model.response.stream.StreamResponsesAndPage;
import com.fleencorp.feen.model.response.stream.attendee.StreamAttendeeResponse;
import com.fleencorp.feen.model.response.stream.base.RetrieveStreamResponse;
import com.fleencorp.feen.model.response.stream.statistic.TotalStreamsAttendedByUserResponse;
import com.fleencorp.feen.model.response.stream.statistic.TotalStreamsCreatedByUserResponse;
import com.fleencorp.feen.model.search.stream.common.StreamSearchResult;
import com.fleencorp.feen.model.security.FleenUser;
import com.fleencorp.feen.service.review.ReviewService;
import com.fleencorp.feen.service.stream.StreamOperationsService;
import com.fleencorp.feen.service.stream.attendee.StreamAttendeeOperationsService;
import com.fleencorp.feen.service.stream.search.StreamSearchService;
import com.fleencorp.localizer.service.Localizer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static com.fleencorp.base.util.FleenUtil.toSearchResult;
import static com.fleencorp.feen.constant.stream.StreamVisibility.PUBLIC;
import static com.fleencorp.feen.constant.stream.attendee.StreamAttendeeRequestToJoinStatus.APPROVED;
import static com.fleencorp.feen.util.CommonUtil.allNonNull;
import static java.util.Objects.nonNull;

/**
 * Implementation of the `StreamSearchService` interface that provides functionality for searching and managing streams.
 *
 * <p>This service handles retrieving streams based on different criteria such as date, title, and user attendance.</p>
 *
 * <p>The service relies on various repositories and services to fetch and process stream data, manage stream attendees,
 * and handle user-specific details like join status and visibility.</p>
 *
 * <p>The service provides the ability to handle different types of stream searches, including streams attended by the
 * current user, streams attended with another user, and streams filtered by time (upcoming, past, or live).</p>
 *
 * <p>Localization of the search result responses is handled as well, ensuring that the response is appropriate based on
 * the user’s locale.</p>
 *
 * @author Yusuf Àlàmù Musa
 * @version 1.0
 */
@Slf4j
@Service
public class StreamSearchServiceImpl implements StreamSearchService {

  private final ReviewService reviewService;
  private final StreamAttendeeOperationsService streamAttendeeOperationsService;
  private final StreamOperationsService streamOperationsService;
  private final StreamMapper streamMapper;
  private final Localizer localizer;

  public StreamSearchServiceImpl(
      final ReviewService reviewService,
      final StreamAttendeeOperationsService streamAttendeeOperationsService,
      final StreamOperationsService streamOperationsService,
      final StreamMapper streamMapper,
      final Localizer localizer) {
    this.streamAttendeeOperationsService = streamAttendeeOperationsService;
    this.reviewService = reviewService;
    this.streamOperationsService = streamOperationsService;
    this.streamMapper = streamMapper;
    this.localizer = localizer;
  }

  /**
   * Finds streams based on the provided search request and the current user.
   * The method retrieves streams using the search request, determines various statuses like schedule and join status
   * based on the user, sets the attendees and total count of attendees for each stream, and fetches additional details
   * like the first 10 attendees. The method also retrieves stream type information and processes the results for the response.
   *
   * <p>This method provides a comprehensive way to filter and retrieve streams, taking into account both user-specific
   * details and search criteria, while also handling attendees and status information.</p>
   *
   * @param searchRequest the search request containing filters, pagination info, and other parameters for stream retrieval
   * @param user the current user, used to determine user-specific stream details and statuses
   * @return a `StreamSearchResult` containing the processed and localized results of the filtered streams
   */
  @Override
  @MeasureExecutionTime
  public StreamSearchResult findStreams(final StreamSearchRequest searchRequest, final FleenUser user) {
    // Find streams based on the search request
    final StreamResponsesAndPage streamResponsesAndPage = findStreams(searchRequest);
    // Get the list of stream views from the search result
    final List<StreamResponse> streamResponses = streamResponsesAndPage.getResponses();
    // Process other details of the streams
    streamOperationsService.processOtherStreamDetails(streamResponses, user);
    // Retrieve the stream type info
    final StreamTypeInfo streamTypeInfo = streamMapper.toStreamTypeInfo(searchRequest.getStreamType());
    // Create the search result
    final StreamSearchResult streamSearchResult = StreamSearchResult.of(toSearchResult(streamResponses, streamResponsesAndPage.getPage()), streamTypeInfo);
    // Return a search result with the responses and pagination details
    return localizer.of(streamSearchResult);
  }

  /**
   * Finds streams based on the provided search request and stream time type (upcoming, past, or live).
   * The method first determines the appropriate page of streams based on the specified time type
   * (upcoming, past, or live) by calling the `findByStreamTimeType` method. It then converts the stream data
   * to response objects and processes the results for the response.
   *
   * <p>The method helps to filter streams based on their time type (upcoming, past, or live) and allows further
   * filtering through the search request.</p>
   *
   * @param searchRequest the search request containing filters, pagination info, and other parameters for stream retrieval
   * @param streamTimeType the type of stream time (upcoming, past, or live)
   * @return a `StreamSearchResult` containing the processed and localized results of the filtered streams
   */
  @Override
  public StreamSearchResult findStreamsPublic(final StreamSearchRequest searchRequest, final StreamTimeType streamTimeType) {
    // Determine the appropriate page of streams based on the stream time type
    final Page<FleenStream> page = findByStreamTimeType(searchRequest, streamTimeType);
    // Convert the page content to FleenStreamResponse objects
    final List<StreamResponse> streamResponses = streamMapper.toStreamResponses(page.getContent());
    // Return the processed and localized response
    return processStreamsCreatedByUserOrAttendedByUserOrAttendedWithAnotherUser(streamResponses, page, searchRequest);
  }

  /**
   * Finds streams based on their time type (upcoming, past, or live) and the given search request.
   * The method filters the streams based on the specified stream time type:
   * - If the time type is "upcoming", it retrieves streams that are scheduled to occur in the future.
   * - If the time type is "past", it retrieves streams that have already occurred.
   * - If the time type is "live", it retrieves streams that are currently live.
   *
   * <p>The method checks the provided `streamTimeType` and calls the corresponding helper methods to retrieve the
   * streams accordingly. If the time type is upcoming, the method delegates to the `getUpcomingStreams` method. If the
   * time type is past, it delegates to the `getPastStreams` method. Otherwise, it defaults to the `getLiveStreams` method.</p>
   *
   * @param searchRequest the search request containing filters, pagination info, and other parameters for stream retrieval
   * @param streamTimeType the type of stream time (upcoming, past, or live)
   * @return a paginated result of `FleenStream` objects based on the given search criteria and stream time type
   */
  protected Page<FleenStream> findByStreamTimeType(final StreamSearchRequest searchRequest, final StreamTimeType streamTimeType) {
    if (StreamTimeType.isUpcoming(streamTimeType)) {
      return getUpcomingStreams(searchRequest);
    } else if (StreamTimeType.isPast(streamTimeType)) {
      return getPastStreams(searchRequest);
    } else {
      return getLiveStreams(searchRequest);
    }
  }

  /**
   * Finds and retrieves the streams associated with the current user, with optional filtering by date range, title, and visibility.
   * The method supports filtering by both start and end dates, title, and stream visibility.
   * If no filters are applied, it retrieves all streams associated with the user.
   *
   * <p>If both start and end dates and the visibility are provided in the search request, the method retrieves streams
   * attended by the user within that date range and visibility. If only dates are set, it filters by date range only.</p>
   *
   * <p>If a title is provided along with visibility, the method filters streams by both title and visibility. If only a
   * title is provided, the method filters streams by the title. If no other filters are applied, all streams for the user
   * are retrieved.</p>
   *
   * <p>After retrieving the streams, the method converts them into response views, updates relevant details such as join
   * status and schedule based on the user's timezone, and then returns a search result containing the streams with pagination
   * and stream type information.</p>
   *
   * @param searchRequest the request object containing search parameters such as date range, title, visibility, and pagination info
   * @param user the user whose streams are being searched
   * @return a localized response containing the streams associated with the user, including any filtering applied and pagination details
   */
  @Override
  public StreamSearchResult findMyStreams(final StreamSearchRequest searchRequest, final FleenUser user) {
    final Page<FleenStream> page = findMyStreams(searchRequest, user.toMember());
    // Create and return the search result
    return processStreamsAndReturn(searchRequest, user, page);
  }

  /**
   * Retrieves a paginated list of {@link FleenStream} entities owned by the given member,
   * based on the filtering criteria provided in the {@link StreamSearchRequest}.
   *
   * <p>The method applies conditional logic to determine which filters are present in the request.
   * If both start and end dates are provided along with stream visibility, it filters by date range and visibility.
   * If only the date range is provided, it filters by that range alone. If the title and visibility are both set,
   * it filters streams based on title and visibility. If only the title is present, it filters by title.
   * If no filters are specified, it retrieves all streams created by the member.</p>
   *
   * @param searchRequest the request containing optional filters such as title, date range, and visibility
   * @param member the member whose owned streams are to be retrieved
   * @return a paginated list of {@link FleenStream} entities owned by the member
   */
  protected Page<FleenStream> findMyStreams(final StreamSearchRequest searchRequest, final Member member) {
    final Page<FleenStream> page;
    final Pageable pageable = searchRequest.getPage();
    final String title = searchRequest.getTitle();
    final LocalDateTime startDateTime = searchRequest.getStartDateTime();
    final LocalDateTime endDateTime = searchRequest.getEndDateTime();
    final StreamVisibility streamVisibility = searchRequest.getVisibility(PUBLIC);
    final boolean isDateSet = searchRequest.areAllDatesSet();

    if (isDateSet && nonNull(streamVisibility)) {
      // Filter by date range and visibility, if both are set
      page = streamOperationsService.findByDateBetweenAndUser(startDateTime, endDateTime, streamVisibility, member, pageable);
    } else if (isDateSet) {
      // Filter by date range, if only dates are set
      page = streamOperationsService.findByDateBetweenAndUser(startDateTime, endDateTime, member, pageable);
    } else if (allNonNull(title, streamVisibility)) {
      // Filter by title and visibility, if both are set
      page = streamOperationsService.findByTitleAndUser(title, streamVisibility, member, pageable);
    } else if (nonNull(title)) {
      // Filter by title, if only the title is set
      page = streamOperationsService.findByTitleAndUser(title, member, pageable);
    } else {
      // Retrieve all streams for the user, if no other filters apply
      page = streamOperationsService.findManyByMe(member, pageable);
    }

    return page;
  }

  /**
   * Finds and retrieves streams attended by a specific user, with optional filtering by date range or stream title.
   * If both start and end dates are set in the search request, it retrieves streams attended within that date range.
   * If a stream title is provided, it filters streams by the title.
   * If no filters are applied, it retrieves all streams attended by the user.
   *
   * <p>The method first checks if both the start and end dates are provided. If they are, it filters the streams
   * attended by the user within that date range. If the title is provided instead, the method filters streams by the title.
   * If neither filter is applied, it retrieves all streams attended by the user.</p>
   *
   * <p>After retrieving the streams, the method converts them into response views, processing the streams with relevant
   * details such as attendees and stream type, and then returns the search result containing the streams with pagination
   * and stream type information.</p>
   *
   * @param searchRequest the request object containing search parameters such as the date range, title, and pagination info
   * @param user the user whose attended streams are being searched
   * @return a localized response containing the streams attended by the user, including any filtering applied and pagination details
   */
  @Override
  public StreamSearchResult findStreamsAttendedByUser(final StreamSearchRequest searchRequest, final FleenUser user) {
    final Member member = user.toMember();
    final Page<FleenStream> page = findStreamsAttendedByUser(searchRequest, member);

    // Create and return the search result
    return processStreamsAndReturn(searchRequest, user, page);
  }

  /**
   * Retrieves a paginated list of {@link FleenStream} entities that the given member has attended,
   * based on the criteria provided in the {@link StreamSearchRequest}.
   *
   * <p>If both the start and end dates are present in the request, the search is filtered by the
   * specified date range. If only the title is provided, it filters streams by title. If no filters
   * are set, it returns all streams the member has attended. Pagination is applied using the
   * {@link Pageable} object from the request.</p>
   *
   * @param searchRequest the request containing optional filters such as title and date range
   * @param member the member whose attended streams are to be retrieved
   * @return a paginated list of {@link FleenStream} instances attended by the member
   */
  private Page<FleenStream> findStreamsAttendedByUser(final StreamSearchRequest searchRequest, final Member member) {
    final Page<FleenStream> page;
    final Pageable pageable = searchRequest.getPage();
    final String title = searchRequest.getTitle();
    final LocalDateTime startDateTime = searchRequest.getStartDateTime();
    final LocalDateTime endDateTime = searchRequest.getEndDateTime();

    if (searchRequest.areAllDatesSet()) {
      // Filter by date range if both start and end dates are set
      page = streamOperationsService.findAttendedByDateBetweenAndUser(startDateTime, endDateTime, member, pageable);
    } else if (nonNull(title)) {
      // Filter by title if the title is provided
      page = streamOperationsService.findAttendedByTitleAndUser(title, member, pageable);
    } else {
      // Retrieve all attended streams if no other filters are applied
      page = streamOperationsService.findAttendedByUser(member, pageable);
    }

    return page;
  }

  /**
   * Finds and retrieves streams attended together by the current user and another user.
   * If the `anotherUserId` is provided in the search request, it returns the streams that both users attended.
   * If the `anotherUserId` is not provided, it returns an empty result.
   *
   * <p>This method checks if the `anotherUserId` is present in the request. If it is, the method queries the repository
   * for streams that the current user and the specified other user have attended together. If not, it returns an empty list of streams.</p>
   *
   * <p>Once the streams are retrieved, the method converts the streams to response views and processes them
   * with relevant details like attendees and stream type information. Finally, it returns the search result with
   * the processed streams and pagination information.</p>
   *
   * @param searchRequest the request object containing search parameters such as the other user's ID and pagination info
   * @param user the current user whose attended streams are being searched
   * @return a localized response containing the streams attended together by the current user and another user, or an empty result if no `anotherUserId` is provided
   */
  @Override
  public StreamSearchResult findStreamsAttendedWithAnotherUser(final StreamSearchRequest searchRequest, final FleenUser user) {
    Page<FleenStream> page = new PageImpl<>(List.of());
    final Pageable pageable = searchRequest.getPage();
    final Member member = user.toMember();
    final Member anotherMember = searchRequest.getAnotherUser();

    if (searchRequest.hasAnotherUser()) {
      // Retrieve streams attended together by the current user and another user
      page = streamOperationsService.findStreamsAttendedTogether(member, anotherMember, pageable);
    }

    // Create and return the search result
    return processStreamsAndReturn(searchRequest, user, page);
  }

  /**
   * Processes a paginated list of {@link FleenStream} entities based on the provided search request
   * and user context, and returns a localized {@link StreamSearchResult} containing stream details,
   * pagination information, and stream type metadata.
   *
   * <p>The method maps the list of streams from the page to {@link StreamResponse} DTOs and enhances
   * them with additional details based on the user. It then maps the stream type from the search request
   * to a {@link StreamTypeInfo}, and wraps the result in a {@link SearchResult} that includes
   * pagination metadata. Finally, it combines the result and stream type info into a {@link StreamSearchResult}
   * and localizes the final output using the current locale.</p>
   *
   * @param searchRequest the request containing search criteria, including stream type
   * @param user the currently authenticated user used for contextual stream processing
   * @param page the paginated result of {@link FleenStream} entities to be processed
   * @return a localized {@link StreamSearchResult} containing the processed stream results
   */
  private StreamSearchResult processStreamsAndReturn(final StreamSearchRequest searchRequest, final FleenUser user, final Page<FleenStream> page) {
    // Convert the streams to response views
    final List<StreamResponse> streamResponses = streamMapper.toStreamResponses(page.getContent());
    // Process other details of the streams
    streamOperationsService.processOtherStreamDetails(streamResponses, user);
    // Retrieve the stream type info
    final StreamTypeInfo streamTypeInfo = streamMapper.toStreamTypeInfo(searchRequest.getStreamType());
    // Create a search result
    final SearchResult searchResult = toSearchResult(streamResponses, page);
    // Create a search result with the streams responses and pagination details
    final StreamSearchResult streamSearchResult = StreamSearchResult.of(searchResult, streamTypeInfo);
    // Return a search result
    return localizer.of(streamSearchResult);
  }

  /**
   * Processes and retrieves a search result for streams attended by a user or attended with another user.
   * It sets the attendees and total number of attendees for each stream, as well as the first 10 attendees in any order.
   *
   * <p>This method processes a list of stream responses, sets relevant details for the attendees of each stream (including
   * the first 10 attendees), and retrieves the stream type information. It then returns a search result that includes
   * the stream responses and pagination details.</p>
   *
   * <p>If no streams are found, an empty search result is returned, including the stream type information.</p>
   *
   * @param streamResponses the list of stream responses to process
   * @param page the page of streams to be included in the search result
   * @param searchRequest the request object containing search parameters, such as the stream type
   * @return a localized response containing the search result with stream responses and pagination details
   */
  protected StreamSearchResult processStreamsCreatedByUserOrAttendedByUserOrAttendedWithAnotherUser(final Collection<StreamResponse> streamResponses, final Page<FleenStream> page, final StreamSearchRequest searchRequest) {
    if (nonNull(streamResponses)) {
      streamResponses.stream()
        .filter(Objects::nonNull)
        .forEach(streamResponse -> {
          // Set the attendees and total number of attendees for each stream
          streamOperationsService.setStreamAttendeesAndTotalAttendeesAttending(streamResponse);
          // Retrieve the first 10 attendees in any order
          streamOperationsService.setFirst10AttendeesAttendingInAnyOrderOnStreams(streamResponse);
        });
    }
    // Retrieve the stream type info
    final StreamTypeInfo streamTypeInfo = streamMapper.toStreamTypeInfo(searchRequest.getStreamType());
    // Create the search result
    final StreamSearchResult streamSearchResult = StreamSearchResult.of(toSearchResult(streamResponses, page), streamTypeInfo);
    // Return a search result with the responses and pagination details
    return localizer.of(streamSearchResult);
  }

  /**
   * Retrieves detailed information about a specific stream, including its attendees, schedule, and join status.
   *
   * <p>This method retrieves a stream based on the given stream ID and user details. It fetches the list of attendees,
   * the stream's response details, and updates the stream's schedule based on the user's timezone and join status.
   * Additionally, it counts the total number of attendees whose request to join the stream is approved and are attending.</p>
   *
   * <p>The response includes the stream's details, the attendees going to the stream, and the total number of attendees
   * whose request to join is approved.</p>
   *
   * @param streamId the ID of the stream to retrieve
   * @param user the user for whom the join status and schedule will be updated
   * @return a localized response containing the stream details, attendees, total count, and stream type information
   * @throws StreamNotFoundException if no stream is found with the given ID
   */
  @Override
  public RetrieveStreamResponse retrieveStream(final Long streamId, final FleenUser user) throws StreamNotFoundException {
    // Retrieve the stream by its ID
    final FleenStream stream = streamOperationsService.findStream(streamId);
    // The Stream converted to a response
    final StreamResponse streamResponse = streamMapper.toStreamResponseNoJoinStatus(stream);
    // Add the single response to a List
    final List<StreamResponse> streamResponses = List.of(streamResponse);
    // Get most recent review of the stream
    final ReviewResponse mostRecentReview = reviewService.findMostRecentReview(ReviewParentType.STREAM, streamId, user);
    // Set the reviews
    streamResponse.setReviews(mostRecentReview);
    // Get all stream or stream attendees
    final Collection<StreamAttendeeResponse> attendeesGoingToStream = streamAttendeeOperationsService.getAttendeesGoingToStream(streamResponse);
    // Process other details of the streams
    streamOperationsService.processOtherStreamDetails(streamResponses, user);
    // Count total attendees whose request to join stream is approved and are attending the stream because they are interested
    final long totalAttendees = streamAttendeeOperationsService.countByStreamAndRequestToJoinStatusAndAttending(stream, APPROVED, true);
    // Retrieve the stream type info
    final StreamTypeInfo streamTypeInfo = streamMapper.toStreamTypeInfo(stream.getStreamType());
    // Create the response
    final RetrieveStreamResponse retrieveStreamResponse = RetrieveStreamResponse.of(streamId, streamResponse, attendeesGoingToStream, totalAttendees, streamTypeInfo);
    // Return the localized response
    return localizer.of(retrieveStreamResponse);
  }

  /**
   * Counts the total number of streams created by a user, optionally filtered by stream type.
   *
   * <p>If a stream type is provided in the search request, the count of streams created by the user of that specific type is returned.
   * If no stream type is provided, the count for all stream types created by the user is retrieved.</p>
   *
   * <p>The method retrieves the stream type information and returns the count along with localized response details.</p>
   *
   * @param searchRequest the request containing the optional stream type filter and pagination details
   * @param user the user whose stream creation count is being determined
   *
   * @return a response containing the total number of streams created by the user, along with stream type information
   */
  public TotalStreamsCreatedByUserResponse countTotalStreamsByUser(final StreamTypeSearchRequest searchRequest, final FleenUser user) {
    final Member member = user.toMember();
    final StreamType streamType = searchRequest.getStreamType();

    // Count the attendance by the user
    final Long totalCount = searchRequest.hasStreamType()
      ? streamOperationsService.countTotalStreamsByUser(streamType, member)
      : streamOperationsService.countTotalStreamsByUser(member);

    // Retrieve the stream type info
    final StreamTypeInfo streamTypeInfo = streamMapper.toStreamTypeInfo(streamType);
    // Return the localized response
    return TotalStreamsCreatedByUserResponse.of(totalCount, streamTypeInfo);
  }

  /**
   * Counts the total number of streams attended by a user, optionally filtered by stream type.
   *
   * <p>If a stream type is provided in the search request, the count of streams attended by the user of that specific type is returned.
   * If no stream type is provided, the count for all stream types attended by the user is retrieved.</p>
   *
   * <p>The method retrieves the stream type information and returns the count along with localized response details.</p>
   *
   * @param searchRequest the request containing the optional stream type filter and pagination details
   * @param user the user whose stream attendance is being counted
   *
   * @return a response containing the total number of streams attended by the user, along with stream type information
   */
  public TotalStreamsAttendedByUserResponse countTotalStreamsAttendedByUser(final StreamTypeSearchRequest searchRequest, final FleenUser user) {
    final Member member = user.toMember();
    final StreamType streamType = searchRequest.getStreamType();

    // Count the attendance by the user
    final Long totalCount = searchRequest.hasStreamType()
      ? streamOperationsService.countTotalStreamsAttended(streamType, member)
      : streamOperationsService.countTotalStreamsAttended(member);

    // Retrieve the stream type info
    final StreamTypeInfo streamTypeInfo = streamMapper.toStreamTypeInfo(streamType);
    // Return the localized response
    return TotalStreamsAttendedByUserResponse.of(totalCount, streamTypeInfo);
  }

  /**
   * Retrieves a paginated list of streams based on the provided search criteria.
   * The search can be filtered by date range, title, or return all active streams by default.
   *
   * <p>If both start and end dates are provided, streams within that date range are retrieved. If a title is provided,
   * streams matching the title are fetched. If no filters are applied, all active streams are returned.</p>
   *
   * @param searchRequest the search request containing the optional date range, title, and pagination information
   *
   * @return a response object containing the list of streams and pagination details
   */
  public StreamResponsesAndPage findStreams(final StreamSearchRequest searchRequest) {
    final Page<FleenStream> page;
    final Pageable pageable = searchRequest.getPage();
    final String title = searchRequest.getTitle();
    final LocalDateTime startDateTime = searchRequest.getStartDateTime();
    final LocalDateTime endDateTime = searchRequest.getEndDateTime();

    // If both start and end dates are set, search by date range
    if (searchRequest.areAllDatesSet()) {
      page = streamOperationsService.findByDateBetween(startDateTime, endDateTime, StreamStatus.ACTIVE, pageable);
    } else if (nonNull(title)) {
      // If title is set, search by stream title
      page = streamOperationsService.findByTitle(title, StreamStatus.ACTIVE, pageable);
    } else {
      // Default to searching for active streams without specific filters
      page = streamOperationsService.findMany(StreamStatus.ACTIVE, pageable);
    }

    // Create the responses
    final List<StreamResponse> streamResponses = streamMapper.toStreamResponses(page.getContent());
    // Return the response with a list of FleenStreams and pagination info
    return StreamResponsesAndPage.of(streamResponses, page);
  }

  /**
   * Retrieves a paginated list of upcoming streams based on the search request.
   * If a query parameter is provided, filters streams by title; otherwise, fetches all upcoming streams.
   *
   * <p>If the query parameter is null, all upcoming streams are returned based on the provided stream type and pagination settings.</p>
   *
   * @param searchRequest the search request containing query, stream type, and pagination information
   *
   * @return a page of upcoming streams that match the search criteria
   */
  protected Page<FleenStream> getUpcomingStreams(final StreamSearchRequest searchRequest) {
    if (nonNull(searchRequest.getQ())) {
      return streamOperationsService.findUpcomingStreamsByTitle(searchRequest.getQ(), LocalDateTime.now(), searchRequest.getStreamType(), searchRequest.getPage());
    }
    return streamOperationsService.findUpcomingStreams(LocalDateTime.now(), searchRequest.getStreamType(), searchRequest.getPage());
  }

  /**
   * Retrieves a paginated list of past streams based on the search request.
   * If a query parameter is provided, filters streams by title; otherwise, fetches all past streams.
   *
   * <p>If the query parameter is null, all past streams are returned based on the provided stream type and pagination settings.</p>
   *
   * @param searchRequest the search request containing query, stream type, and pagination information
   *
   * @return a page of past streams that match the search criteria
   */
  protected Page<FleenStream> getPastStreams(final StreamSearchRequest searchRequest) {
    if (nonNull(searchRequest.getQ())) {
      return streamOperationsService.findPastStreamsByTitle(searchRequest.getQ(), LocalDateTime.now(), searchRequest.getStreamType(), searchRequest.getPage());
    }
    return streamOperationsService.findPastStreams(LocalDateTime.now(), searchRequest.getStreamType(), searchRequest.getPage());
  }

  /**
   * Retrieves a paginated list of live streams based on the search request.
   * If a query parameter is provided, filters streams by title; otherwise, fetches all live streams.
   *
   * <p>If the query parameter is null, all live streams are returned based on the provided stream type and pagination settings.</p>
   *
   * @param searchRequest the search request containing query, stream type, and pagination information
   *
   * @return a page of live streams that match the search criteria
   */
  protected Page<FleenStream> getLiveStreams(final StreamSearchRequest searchRequest) {
    if (nonNull(searchRequest.getQ())) {
      return streamOperationsService.findLiveStreamsByTitle(searchRequest.getQ(), LocalDateTime.now(), searchRequest.getStreamType(), searchRequest.getPage());
    }
    return streamOperationsService.findLiveStreams(LocalDateTime.now(), searchRequest.getStreamType(), searchRequest.getPage());
  }

}