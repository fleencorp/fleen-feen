package com.fleencorp.feen.service.impl.stream.common;

import com.fleencorp.feen.constant.stream.StreamStatus;
import com.fleencorp.feen.constant.stream.StreamTimeType;
import com.fleencorp.feen.constant.stream.StreamVisibility;
import com.fleencorp.feen.exception.stream.FleenStreamNotFoundException;
import com.fleencorp.feen.mapper.stream.StreamMapper;
import com.fleencorp.feen.model.domain.stream.FleenStream;
import com.fleencorp.feen.model.domain.stream.StreamAttendee;
import com.fleencorp.feen.model.domain.user.Member;
import com.fleencorp.feen.model.info.stream.StreamTypeInfo;
import com.fleencorp.feen.model.request.search.stream.StreamSearchRequest;
import com.fleencorp.feen.model.request.search.stream.type.StreamTypeSearchRequest;
import com.fleencorp.feen.model.response.stream.FleenStreamResponse;
import com.fleencorp.feen.model.response.stream.StreamResponsesAndPage;
import com.fleencorp.feen.model.response.stream.attendee.StreamAttendeeResponse;
import com.fleencorp.feen.model.response.stream.base.RetrieveStreamResponse;
import com.fleencorp.feen.model.response.stream.statistic.TotalStreamsAttendedByUserResponse;
import com.fleencorp.feen.model.response.stream.statistic.TotalStreamsCreatedByUserResponse;
import com.fleencorp.feen.model.search.stream.common.EmptyStreamSearchResult;
import com.fleencorp.feen.model.search.stream.common.StreamSearchResult;
import com.fleencorp.feen.model.security.FleenUser;
import com.fleencorp.feen.repository.stream.FleenStreamRepository;
import com.fleencorp.feen.repository.stream.StreamAttendeeRepository;
import com.fleencorp.feen.repository.stream.UserFleenStreamRepository;
import com.fleencorp.feen.service.stream.attendee.StreamAttendeeService;
import com.fleencorp.feen.service.stream.common.StreamService;
import com.fleencorp.feen.service.stream.search.StreamSearchService;
import com.fleencorp.localizer.service.Localizer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static com.fleencorp.base.util.FleenUtil.handleSearchResult;
import static com.fleencorp.base.util.FleenUtil.toSearchResult;
import static com.fleencorp.feen.constant.stream.StreamAttendeeRequestToJoinStatus.APPROVED;
import static com.fleencorp.feen.constant.stream.StreamVisibility.PUBLIC;
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

  private final StreamAttendeeService streamAttendeeService;
  private final StreamService streamService;
  private final FleenStreamRepository streamRepository;
  private final StreamAttendeeRepository streamAttendeeRepository;
  private final UserFleenStreamRepository userStreamRepository;
  private final StreamMapper streamMapper;
  private final Localizer localizer;

  /**
   * Constructs a new instance of `StreamSearchServiceImpl` with the provided services and repositories.
   * This constructor initializes the service class with the necessary dependencies required for managing
   * and searching streams, stream attendees, and user-specific stream details.
   *
   * @param streamAttendeeService the service responsible for handling operations related to stream attendees
   * @param streamService the service responsible for handling operations related to streams
   * @param streamRepository the repository used to interact with the `FleenStream` data
   * @param streamAttendeeRepository the repository used to interact with the `StreamAttendee` data
   * @param userStreamRepository the repository used to manage the relationship between users and streams
   * @param localizer the service used for localizing response messages and data
   * @param streamMapper the mapper responsible for converting stream entities to response objects
   */
  public StreamSearchServiceImpl(
      final StreamAttendeeService streamAttendeeService,
      final StreamService streamService,
      final FleenStreamRepository streamRepository,
      final StreamAttendeeRepository streamAttendeeRepository,
      final UserFleenStreamRepository userStreamRepository,
      final Localizer localizer,
      final StreamMapper streamMapper) {
    this.streamAttendeeService = streamAttendeeService;
    this.streamService = streamService;
    this.streamRepository = streamRepository;
    this.streamAttendeeRepository = streamAttendeeRepository;
    this.userStreamRepository = userStreamRepository;
    this.localizer = localizer;
    this.streamMapper = streamMapper;
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
  public StreamSearchResult findStreams(final StreamSearchRequest searchRequest, final FleenUser user) {
    // Find streams based on the search request
    final StreamResponsesAndPage streamResponsesAndPage = findStreams(searchRequest);
    // Get the list of stream views from the search result
    final List<FleenStreamResponse> views = streamResponsesAndPage.getResponses();
    // Determine statuses like schedule, join status, schedules and timezones
    streamService.determineDifferentStatusesAndDetailsOfStreamBasedOnUser(views, user);
    // Set the attendees and total attendee count for each stream
    streamAttendeeService.setStreamAttendeesAndTotalAttendeesAttending(views);
    // Get the first 10 attendees for each stream
    streamAttendeeService.setFirst10AttendeesAttendingInAnyOrderOnStreams(views);
    // Retrieve the stream type info
    final StreamTypeInfo streamTypeInfo = streamMapper.toStreamTypeInfo(searchRequest.getStreamType());
    // Return a search result view with the streams responses and pagination details
    return handleSearchResult(
      streamResponsesAndPage.getPage(),
      localizer.of(StreamSearchResult.of(toSearchResult(views, streamResponsesAndPage.getPage()), streamTypeInfo)),
      localizer.of(EmptyStreamSearchResult.of(toSearchResult(List.of(), streamResponsesAndPage.getPage()), streamTypeInfo))
    );
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
  public StreamSearchResult findStreams(final StreamSearchRequest searchRequest, final StreamTimeType streamTimeType) {
    // Determine the appropriate page of streams based on the stream time type
    final Page<FleenStream> page = findByStreamTimeType(searchRequest, streamTimeType);
    // Convert the page content to FleenStreamResponse objects
    final List<FleenStreamResponse> views = streamMapper.toFleenStreamResponses(page.getContent());
    // Return the processed and localized response
    return processStreamsCreatedByUserOrAttendedByUserOrAttendedWithAnotherUser(views, page, searchRequest);
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
    final Page<FleenStream> page;
    final StreamVisibility streamVisibility = searchRequest.getVisibility(PUBLIC);

    if (searchRequest.areAllDatesSet() && nonNull(searchRequest.getStreamVisibility())) {
      // Filter by date range and visibility, if both are set
      page = userStreamRepository.findByDateBetweenAndUser(searchRequest.getStartDateTime(), searchRequest.getEndDateTime(),
          streamVisibility, user.toMember(), searchRequest.getPage());
    } else if (searchRequest.areAllDatesSet()) {
      // Filter by date range, if only dates are set
      page = userStreamRepository.findByDateBetweenAndUser(searchRequest.getStartDateTime(), searchRequest.getEndDateTime(),
          user.toMember(), searchRequest.getPage());
    } else if (nonNull(searchRequest.getTitle()) && nonNull(searchRequest.getStreamVisibility())) {
      // Filter by title and visibility, if both are set
      page = userStreamRepository.findByTitleAndUser(searchRequest.getTitle(), streamVisibility, user.toMember(), searchRequest.getPage());
    } else if (nonNull(searchRequest.getTitle())) {
      // Filter by title, if only the title is set
      page = userStreamRepository.findByTitleAndUser(searchRequest.getTitle(), user.toMember(), searchRequest.getPage());
    } else {
      // Retrieve all streams for the user, if no other filters apply
      page = userStreamRepository.findManyByMe(user.toMember(), searchRequest.getPage());
    }

    // Convert the streams to response views
    final List<FleenStreamResponse> views = streamMapper.toFleenStreamResponses(page.getContent());
    // Determine the various status of the streams user
    streamService.determineUserJoinStatusForStream(views, user);
    // Set other schedule details if user timezone is different
    streamService.setOtherScheduleBasedOnUserTimezone(views, user);
    // Return the processed and localized response
    return processStreamsCreatedByUserOrAttendedByUserOrAttendedWithAnotherUser(views, page, searchRequest);
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
    final Page<FleenStream> page;

    if (searchRequest.areAllDatesSet()) {
      // Filter by date range if both start and end dates are set
      page = userStreamRepository.findAttendedByDateBetweenAndUser(searchRequest.getStartDateTime(), searchRequest.getEndDateTime(),
          user.toMember(), searchRequest.getPage());
    } else if (nonNull(searchRequest.getTitle())) {
      // Filter by title if the title is provided
      page = userStreamRepository.findAttendedByTitleAndUser(searchRequest.getTitle(), user.toMember(), searchRequest.getPage());
    } else {
      // Retrieve all attended streams if no other filters are applied
      page = userStreamRepository.findAttendedByUser(user.toMember(), searchRequest.getPage());
    }

    // Convert the streams to response views
    final List<FleenStreamResponse> views = streamMapper.toFleenStreamResponsesNoJoinStatus(page.getContent());
    // Return the processed and localized response
    return processStreamsCreatedByUserOrAttendedByUserOrAttendedWithAnotherUser(views, page, searchRequest);
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
    final Page<FleenStream> page;

    if (nonNull(searchRequest.getAnotherUserId())) {
      // Retrieve streams attended together by the current user and another user
      page = userStreamRepository.findStreamsAttendedTogether(user.toMember(), Member.of(searchRequest.getAnotherUserId()), searchRequest.getPage());
    } else {
      // Return an empty result if anotherUserId is not provided
      page = new PageImpl<>(List.of());
    }

    // Convert the streams to response views
    final List<FleenStreamResponse> views = streamMapper.toFleenStreamResponsesNoJoinStatus(page.getContent());
    // Return the processed and localized response
    return processStreamsCreatedByUserOrAttendedByUserOrAttendedWithAnotherUser(views, page, searchRequest);
  }

  /**
   * Processes and retrieves a search result view for streams attended by a user or attended with another user.
   * It sets the attendees and total number of attendees for each stream, as well as the first 10 attendees in any order.
   *
   * <p>This method processes a list of stream responses, sets relevant details for the attendees of each stream (including
   * the first 10 attendees), and retrieves the stream type information. It then returns a search result view that includes
   * the stream responses and pagination details.</p>
   *
   * <p>If no streams are found, an empty search result is returned, including the stream type information.</p>
   *
   * @param views the list of stream responses to process
   * @param page the page of streams to be included in the search result
   * @param searchRequest the request object containing search parameters, such as the stream type
   * @return a localized response containing the search result with stream responses and pagination details
   */
  protected StreamSearchResult processStreamsCreatedByUserOrAttendedByUserOrAttendedWithAnotherUser(final List<FleenStreamResponse> views, final Page<FleenStream> page, final StreamSearchRequest searchRequest) {
    // Set the attendees and total number of attendees for each stream
    streamAttendeeService.setStreamAttendeesAndTotalAttendeesAttending(views);
    // Retrieve the first 10 attendees in any order
    streamAttendeeService.setFirst10AttendeesAttendingInAnyOrderOnStreams(views);
    // Retrieve the stream type info
    final StreamTypeInfo streamTypeInfo = streamMapper.toStreamTypeInfo(searchRequest.getStreamType());
    // Return a search result view with the streams responses and pagination details
    return handleSearchResult(
      page,
      localizer.of(StreamSearchResult.of(toSearchResult(views, page), streamTypeInfo)),
      localizer.of(EmptyStreamSearchResult.of(toSearchResult(List.of(), page), streamTypeInfo))
    );
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
   * @throws FleenStreamNotFoundException if no stream is found with the given ID
   */
  @Override
  public RetrieveStreamResponse retrieveStream(final Long streamId, final FleenUser user) throws FleenStreamNotFoundException {
    // Retrieve the stream by its ID
    final FleenStream stream = streamService.findStream(streamId);
    // Get all stream or stream attendees
    final Set<StreamAttendee> streamAttendeesGoingToStream = streamAttendeeService.getAttendeesGoingToStream(stream);
    // The Stream converted to a response
    final FleenStreamResponse streamResponse = streamMapper.toFleenStreamResponseNoJoinStatus(stream);
    // Convert the attendees to response objects
    final Set<StreamAttendeeResponse> streamAttendees = streamAttendeeService.toStreamAttendeeResponsesSet(streamResponse, streamAttendeesGoingToStream);
    // Update the schedule, timezone details and join status
    updateStreamOtherScheduleAndUserJoinStatus(streamResponse, user);
    // Count total attendees whose request to join stream is approved and are attending the stream because they are interested
    final long totalAttendees = streamAttendeeRepository.countByFleenStreamAndRequestToJoinStatusAndAttending(stream, APPROVED, true);
    // Retrieve the stream type info
    final StreamTypeInfo streamTypeInfo = streamMapper.toStreamTypeInfo(stream.getStreamType());
    // Return the localized response
    return localizer.of(RetrieveStreamResponse.of(streamId, streamResponse, streamAttendees, totalAttendees, streamTypeInfo));
  }

  /**
   * Updates the stream's schedule and the user's join status based on the provided stream response and user.
   *
   * <p>This method updates the schedule details of the stream according to the user's timezone if applicable,
   * and determines whether the user is attending or has joined the stream.</p>
   *
   * <p>If the stream response is provided, the method adds it to a list and calls the necessary service methods
   * to adjust the stream's schedule and the user's join status.</p>
   *
   * @param streamResponse the stream response containing the stream details to be updated
   * @param user the user whose timezone and join status are used to update the stream's details
   */
  public void updateStreamOtherScheduleAndUserJoinStatus(final FleenStreamResponse streamResponse, final FleenUser user) {
    if (nonNull(streamResponse)) {
      // Add the single response to a List
      final List<FleenStreamResponse> streams = List.of(streamResponse);
      // Set other schedule details if user timezone is different
      streamService.setOtherScheduleBasedOnUserTimezone(streams, user);
      // Determine the join status of the user if available
      streamService.determineUserJoinStatusForStream(streams, user);
    }
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
    final Long totalCount;

    if (nonNull(searchRequest.getStreamType())) {
      totalCount = userStreamRepository.countTotalStreamsByUser(searchRequest.getStreamType(), user.toMember());
    } else {
      totalCount = userStreamRepository.countTotalStreamsByUser(user.toMember());
    }
    // Retrieve the stream type info
    final StreamTypeInfo streamTypeInfo = streamMapper.toStreamTypeInfo(searchRequest.getStreamType());
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
    final Long totalCount;

    if (nonNull(searchRequest.getStreamType())) {
      totalCount = userStreamRepository.countTotalStreamsAttended(searchRequest.getStreamType(), user.toMember());
    } else {
      totalCount = userStreamRepository.countTotalStreamsAttended(user.toMember());
    }
    // Retrieve the stream type info
    final StreamTypeInfo streamTypeInfo = streamMapper.toStreamTypeInfo(searchRequest.getStreamType());
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

    // If both start and end dates are set, search by date range
    if (searchRequest.areAllDatesSet()) {
      page = streamRepository.findByDateBetween(searchRequest.getStartDateTime(), searchRequest.getEndDateTime(), StreamStatus.ACTIVE, searchRequest.getPage());
    } else if (nonNull(searchRequest.getTitle())) {
      // If title is set, search by stream title
      page = streamRepository.findByTitle(searchRequest.getTitle(), StreamStatus.ACTIVE, searchRequest.getPage());
    } else {
      // Default to searching for active streams without specific filters
      page = streamRepository.findMany(StreamStatus.ACTIVE, searchRequest.getPage());
    }

    // Return the response with a list of FleenStreams and pagination info
    return StreamResponsesAndPage.of(streamMapper.toFleenStreamResponses(page.getContent()), page);
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
      return streamRepository.findUpcomingStreamsByTitle(searchRequest.getQ(), LocalDateTime.now(), searchRequest.getStreamType(), searchRequest.getPage());
    }
    return streamRepository.findUpcomingStreams(LocalDateTime.now(), searchRequest.getStreamType(), searchRequest.getPage());
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
      return streamRepository.findPastStreamsByTitle(searchRequest.getQ(), LocalDateTime.now(), searchRequest.getStreamType(), searchRequest.getPage());
    }
    return streamRepository.findPastStreams(LocalDateTime.now(), searchRequest.getStreamType(), searchRequest.getPage());
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
      return streamRepository.findLiveStreamsByTitle(searchRequest.getQ(), LocalDateTime.now(), searchRequest.getStreamType(), searchRequest.getPage());
    }
    return streamRepository.findLiveStreams(LocalDateTime.now(), searchRequest.getStreamType(), searchRequest.getPage());
  }


}