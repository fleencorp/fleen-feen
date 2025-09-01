package com.fleencorp.feen.stream.service.impl.search;

import com.fleencorp.base.model.view.search.SearchResult;
import com.fleencorp.feen.common.aspect.MeasureExecutionTime;
import com.fleencorp.feen.review.constant.ReviewParentType;
import com.fleencorp.feen.review.model.response.base.ReviewResponse;
import com.fleencorp.feen.review.service.ReviewSearchService;
import com.fleencorp.feen.stream.constant.core.StreamStatus;
import com.fleencorp.feen.stream.constant.core.StreamTimeType;
import com.fleencorp.feen.stream.constant.core.StreamType;
import com.fleencorp.feen.stream.constant.core.StreamVisibility;
import com.fleencorp.feen.stream.exception.core.StreamNotFoundException;
import com.fleencorp.feen.stream.mapper.StreamMapper;
import com.fleencorp.feen.stream.model.domain.FleenStream;
import com.fleencorp.feen.stream.model.info.core.StreamTypeInfo;
import com.fleencorp.feen.stream.model.request.search.StreamSearchRequest;
import com.fleencorp.feen.stream.model.request.search.StreamTypeSearchRequest;
import com.fleencorp.feen.stream.model.response.StreamResponse;
import com.fleencorp.feen.stream.model.response.StreamResponsesAndPage;
import com.fleencorp.feen.stream.model.response.attendee.StreamAttendeeResponse;
import com.fleencorp.feen.stream.model.response.base.RetrieveStreamResponse;
import com.fleencorp.feen.stream.model.response.statistic.TotalStreamsAttendedByUserResponse;
import com.fleencorp.feen.stream.model.response.statistic.TotalStreamsCreatedByUserResponse;
import com.fleencorp.feen.stream.model.search.common.StreamSearchResult;
import com.fleencorp.feen.stream.model.search.common.UserCreatedStreamsSearchResult;
import com.fleencorp.feen.stream.model.search.mutual.MutualStreamAttendanceSearchResult;
import com.fleencorp.feen.stream.service.attendee.StreamAttendeeOperationsService;
import com.fleencorp.feen.stream.service.common.StreamOperationsService;
import com.fleencorp.feen.stream.service.common.StreamQueryService;
import com.fleencorp.feen.stream.service.search.StreamSearchService;
import com.fleencorp.feen.user.model.domain.Member;
import com.fleencorp.feen.shared.security.RegisteredUser;
import com.fleencorp.feen.user.service.member.MemberService;
import com.fleencorp.localizer.service.Localizer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static com.fleencorp.base.util.FleenUtil.toSearchResult;
import static com.fleencorp.feen.common.util.common.CommonUtil.allNonNull;
import static com.fleencorp.feen.stream.constant.attendee.StreamAttendeeRequestToJoinStatus.APPROVED;
import static com.fleencorp.feen.stream.constant.core.StreamVisibility.PUBLIC;
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

  private final MemberService memberService;
  private final ReviewSearchService reviewSearchService;
  private final StreamAttendeeOperationsService streamAttendeeOperationsService;
  private final StreamOperationsService streamOperationsService;
  private final StreamQueryService streamQueryService;
  private final StreamMapper streamMapper;
  private final Localizer localizer;

  public StreamSearchServiceImpl(
      final MemberService memberService,
      final ReviewSearchService reviewSearchService,
      final StreamAttendeeOperationsService streamAttendeeOperationsService,
      final StreamOperationsService streamOperationsService,
      @Qualifier("streamQueryService") final StreamQueryService streamQueryService,
      final StreamMapper streamMapper,
      final Localizer localizer) {
    this.memberService = memberService;
    this.reviewSearchService = reviewSearchService;
    this.streamAttendeeOperationsService = streamAttendeeOperationsService;
    this.streamOperationsService = streamOperationsService;
    this.streamQueryService = streamQueryService;
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
  public StreamSearchResult findStreams(final StreamSearchRequest searchRequest, final RegisteredUser user) {
    // Find streams based on the search request
    final StreamResponsesAndPage streamResponsesAndPage = findStreams(searchRequest);
    // Get the list of stream views from the search result
    final List<StreamResponse> streamResponses = streamResponsesAndPage.getResponses();
    // Process other details of the streams
    streamOperationsService.processOtherStreamDetails(streamResponses, user.toMember());
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
  public StreamSearchResult findMyStreams(final StreamSearchRequest searchRequest, final RegisteredUser user) {
    final Page<FleenStream> page = findMyStreams(searchRequest, user.toMember());
    // Convert the streams to response views
    final List<StreamResponse> streamResponses = streamMapper.toStreamResponses(page.getContent());
    // Create and return the search result
    return processStreamsAndReturn(streamResponses, searchRequest, user.toMember(), page);
  }

  /**
   * Finds streams created by a specific user based on the given search criteria.
   *
   * <p>This method retrieves the {@link Member} specified in the {@code searchRequest},
   * fetches the streams created by this member using {@link #findMyStreams(StreamSearchRequest, Member)},
   * and processes them to return a {@link StreamSearchResult}.</p>
   *
   * @param searchRequest the search request containing filters and the target user whose streams are to be found
   * @return a {@link StreamSearchResult} containing the processed streams created by the specified user
   */
  @Override
  public UserCreatedStreamsSearchResult findStreamsCreatedByUser(final StreamSearchRequest searchRequest) {
    Member member = searchRequest.getAnotherUser();
    // Get the member
    member = memberService.findMember(member.getMemberId());

    final Page<FleenStream> page = findMyStreams(searchRequest, member);
    // Convert the streams to response views
    final List<StreamResponse> streamResponses = streamMapper.toStreamResponses(page.getContent());
    // Create and return the search result
    final StreamSearchResult streamSearchResult = processStreamsAndReturn(streamResponses, searchRequest, member, page);
    // Create user search result
    final UserCreatedStreamsSearchResult userCreatedStreamsSearchResult = UserCreatedStreamsSearchResult.of(streamSearchResult.getResult(), member.getFullName());
    // Return the localized response
    return localizer.of(userCreatedStreamsSearchResult);
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
      page = streamQueryService.findByDateBetweenAndUser(startDateTime, endDateTime, streamVisibility, member, pageable);
    } else if (isDateSet) {
      page = streamQueryService.findByDateBetweenAndUser(startDateTime, endDateTime, member, pageable);
    } else if (allNonNull(title, streamVisibility)) {
      page = streamQueryService.findByTitleAndUser(title, streamVisibility, member, pageable);
    } else if (nonNull(title)) {
      page = streamQueryService.findByTitleAndUser(title, member, pageable);
    } else {
      page = streamQueryService.findManyByMe(member, pageable);
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
  public StreamSearchResult findStreamsAttendedByUser(final StreamSearchRequest searchRequest, final RegisteredUser user) {
    final Member member = user.toMember();
    final Page<FleenStream> page = findStreamsAttendedByUser(searchRequest, member);
    // Convert the streams to response views
    final List<StreamResponse> streamResponses = streamMapper.toStreamResponses(page.getContent());
    // Create and return the search result
    return processStreamsAndReturn(streamResponses, searchRequest, user.toMember(), page);
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
      page = streamQueryService.findAttendedByDateBetweenAndUser(startDateTime, endDateTime, member, pageable);
    } else if (nonNull(title)) {
      page = streamQueryService.findAttendedByTitleAndUser(title, member, pageable);
    } else {
      page = streamQueryService.findAttendedByUser(member, pageable);
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
  public MutualStreamAttendanceSearchResult findStreamsAttendedWithAnotherUser(final StreamSearchRequest searchRequest, final RegisteredUser user) {
    Page<FleenStream> page = new PageImpl<>(List.of());
    final Pageable pageable = searchRequest.getPage();
    final Member member = user.toMember();
    final Member anotherMember = searchRequest.getAnotherUser();

    if (searchRequest.hasAnotherUser()) {
      // Retrieve streams attended together by the current user and another user
      page = streamQueryService.findStreamsAttendedTogether(member, anotherMember, pageable);
    }

    final List<StreamResponse> streamResponses = streamMapper.toStreamResponses(page.getContent());
    final StreamSearchResult streamSearchResult = processStreamsAndReturn(streamResponses, searchRequest, member, page);
    final MutualStreamAttendanceSearchResult mutualStreamAttendanceSearchResult = MutualStreamAttendanceSearchResult.of(streamSearchResult.getResult(), member.getFullName());

    return localizer.of(mutualStreamAttendanceSearchResult);
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
   * @param member a user or member in the system
   * @param page the paginated result of {@link FleenStream} entities to be processed
   * @return a localized {@link StreamSearchResult} containing the processed stream results
   */
  private StreamSearchResult processStreamsAndReturn(final Collection<StreamResponse> streamResponses, final StreamSearchRequest searchRequest, final Member member, final Page<FleenStream> page) {
    streamOperationsService.processOtherStreamDetails(streamResponses, member);
    final StreamTypeInfo streamTypeInfo = streamMapper.toStreamTypeInfo(searchRequest.getStreamType());

    final SearchResult searchResult = toSearchResult(streamResponses, page);
    final StreamSearchResult streamSearchResult = StreamSearchResult.of(searchResult, streamTypeInfo);

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

    final StreamTypeInfo streamTypeInfo = streamMapper.toStreamTypeInfo(searchRequest.getStreamType());
    final StreamSearchResult streamSearchResult = StreamSearchResult.of(toSearchResult(streamResponses, page), streamTypeInfo);
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
  public RetrieveStreamResponse retrieveStream(final Long streamId, final RegisteredUser user) throws StreamNotFoundException {
    final FleenStream stream = streamQueryService.findStream(streamId);
    final StreamResponse streamResponse = streamMapper.toStreamResponseNoJoinStatus(stream);
    final List<StreamResponse> streamResponses = List.of(streamResponse);

    final ReviewResponse mostRecentReview = reviewSearchService.findMostRecentReview(ReviewParentType.STREAM, streamId, user);
    streamResponse.setReviews(mostRecentReview);
    final Collection<StreamAttendeeResponse> attendeesGoingToStream = streamAttendeeOperationsService.getAttendeesGoingToStream(streamResponse);

    streamOperationsService.processOtherStreamDetails(streamResponses, user.toMember());
    final int totalAttendees = streamAttendeeOperationsService.countByStreamAndRequestToJoinStatusAndAttending(stream, APPROVED, true);
    final StreamTypeInfo streamTypeInfo = streamMapper.toStreamTypeInfo(stream.getStreamType());
    final RetrieveStreamResponse retrieveStreamResponse = RetrieveStreamResponse.of(streamId, streamResponse, attendeesGoingToStream, totalAttendees, streamTypeInfo);
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
  public TotalStreamsCreatedByUserResponse countTotalStreamsByUser(final StreamTypeSearchRequest searchRequest, final RegisteredUser user) {
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
  public TotalStreamsAttendedByUserResponse countTotalStreamsAttendedByUser(final StreamTypeSearchRequest searchRequest, final RegisteredUser user) {
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

    if (searchRequest.areAllDatesSet()) {
      page = streamQueryService.findByDateBetween(startDateTime, endDateTime, StreamStatus.ACTIVE, pageable);
    } else if (nonNull(title)) {
      page = streamQueryService.findByTitle(title, StreamStatus.ACTIVE, pageable);
    } else {
      page = streamQueryService.findMany(StreamStatus.ACTIVE, pageable);
    }

    final List<StreamResponse> streamResponses = streamMapper.toStreamResponses(page.getContent());
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
      return streamQueryService.findUpcomingStreamsByTitle(searchRequest.getQ(), LocalDateTime.now(), searchRequest.getStreamType(), searchRequest.getPage());
    }
    return streamQueryService.findUpcomingStreams(LocalDateTime.now(), searchRequest.getStreamType(), searchRequest.getPage());
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
      return streamQueryService.findPastStreamsByTitle(searchRequest.getQ(), LocalDateTime.now(), searchRequest.getStreamType(), searchRequest.getPage());
    }
    return streamQueryService.findPastStreams(LocalDateTime.now(), searchRequest.getStreamType(), searchRequest.getPage());
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
      return streamQueryService.findLiveStreamsByTitle(searchRequest.getQ(), LocalDateTime.now(), searchRequest.getStreamType(), searchRequest.getPage());
    }
    return streamQueryService.findLiveStreams(LocalDateTime.now(), searchRequest.getStreamType(), searchRequest.getPage());
  }

}