package com.fleencorp.feen.service.impl.stream;

import com.fleencorp.base.model.view.search.SearchResultView;
import com.fleencorp.feen.constant.stream.StreamAttendeeRequestToJoinStatus;
import com.fleencorp.feen.constant.stream.StreamTimeType;
import com.fleencorp.feen.constant.stream.StreamVisibility;
import com.fleencorp.feen.event.publisher.StreamEventPublisher;
import com.fleencorp.feen.exception.base.FailedOperationException;
import com.fleencorp.feen.exception.calendar.CalendarNotFoundException;
import com.fleencorp.feen.exception.stream.CannotCancelOrDeleteOngoingStreamException;
import com.fleencorp.feen.exception.stream.FleenStreamNotFoundException;
import com.fleencorp.feen.mapper.CommonMapper;
import com.fleencorp.feen.mapper.FleenStreamMapper;
import com.fleencorp.feen.mapper.StreamAttendeeMapper;
import com.fleencorp.feen.model.domain.calendar.Calendar;
import com.fleencorp.feen.model.domain.chat.ChatSpaceMember;
import com.fleencorp.feen.model.domain.notification.Notification;
import com.fleencorp.feen.model.domain.stream.FleenStream;
import com.fleencorp.feen.model.domain.stream.StreamAttendee;
import com.fleencorp.feen.model.domain.user.Member;
import com.fleencorp.feen.model.dto.event.*;
import com.fleencorp.feen.model.dto.event.CreateCalendarEventDto.EventAttendeeOrGuest;
import com.fleencorp.feen.model.dto.stream.JoinEventOrStreamDto;
import com.fleencorp.feen.model.dto.stream.ProcessAttendeeRequestToJoinEventOrStreamDto;
import com.fleencorp.feen.model.dto.stream.RequestToJoinEventOrStreamDto;
import com.fleencorp.feen.model.dto.stream.UpdateEventOrStreamVisibilityDto;
import com.fleencorp.feen.model.event.AddCalendarEventAttendeesEvent;
import com.fleencorp.feen.model.info.stream.StreamStatusInfo;
import com.fleencorp.feen.model.info.stream.attendee.StreamAttendeeRequestToJoinStatusInfo;
import com.fleencorp.feen.model.info.JoinStatusInfo;
import com.fleencorp.feen.model.request.calendar.event.*;
import com.fleencorp.feen.model.request.search.calendar.EventSearchRequest;
import com.fleencorp.feen.model.request.search.stream.StreamAttendeeSearchRequest;
import com.fleencorp.feen.model.response.broadcast.RetrieveStreamResponse;
import com.fleencorp.feen.model.response.event.*;
import com.fleencorp.feen.model.response.stream.EventOrStreamAttendeesResponse;
import com.fleencorp.feen.model.response.stream.PageAndFleenStreamResponse;
import com.fleencorp.feen.model.response.stream.RetrieveEventOrStreamResponse;
import com.fleencorp.feen.model.response.stream.StreamAttendeeResponse;
import com.fleencorp.feen.model.response.stream.base.FleenStreamResponse;
import com.fleencorp.feen.model.search.broadcast.request.EmptyRequestToJoinSearchResult;
import com.fleencorp.feen.model.search.broadcast.request.RequestToJoinSearchResult;
import com.fleencorp.feen.model.search.event.EmptyEventSearchResult;
import com.fleencorp.feen.model.search.event.EventSearchResult;
import com.fleencorp.feen.model.search.stream.attendee.EmptyStreamAttendeeSearchResult;
import com.fleencorp.feen.model.search.stream.attendee.StreamAttendeeSearchResult;
import com.fleencorp.feen.model.security.FleenUser;
import com.fleencorp.feen.repository.chat.ChatSpaceMemberRepository;
import com.fleencorp.feen.repository.stream.FleenStreamRepository;
import com.fleencorp.feen.repository.stream.StreamAttendeeRepository;
import com.fleencorp.feen.repository.stream.UserFleenStreamRepository;
import com.fleencorp.feen.repository.user.MemberRepository;
import com.fleencorp.feen.service.common.CountryService;
import com.fleencorp.feen.service.common.MiscService;
import com.fleencorp.feen.service.i18n.LocalizedResponse;
import com.fleencorp.feen.service.impl.notification.NotificationMessageService;
import com.fleencorp.feen.service.impl.stream.base.StreamService;
import com.fleencorp.feen.service.impl.stream.update.EventUpdateService;
import com.fleencorp.feen.service.notification.NotificationService;
import com.fleencorp.feen.service.stream.EventService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.fleencorp.base.util.ExceptionUtil.checkIsNullAny;
import static com.fleencorp.base.util.ExceptionUtil.checkIsTrue;
import static com.fleencorp.base.util.FleenUtil.handleSearchResult;
import static com.fleencorp.base.util.FleenUtil.toSearchResult;
import static com.fleencorp.feen.constant.stream.StreamAttendeeRequestToJoinStatus.APPROVED;
import static com.fleencorp.feen.constant.stream.StreamAttendeeRequestToJoinStatus.PENDING;
import static com.fleencorp.feen.constant.stream.StreamVisibility.PUBLIC;
import static com.fleencorp.feen.validator.impl.TimezoneValidValidator.getAvailableTimezones;
import static java.util.Objects.nonNull;

/**
 * Implementation of the EventService interface.
 * This class provides methods for managing events.
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
@Slf4j
@Service
public class EventServiceImpl extends StreamService implements EventService {

  private final String delegatedAuthorityEmail;
  private final EventUpdateService eventUpdateService;
  private final NotificationMessageService notificationMessageService;
  private final NotificationService notificationService;
  private final ChatSpaceMemberRepository chatSpaceMemberRepository;
  private final MemberRepository memberRepository;
  private final UserFleenStreamRepository userFleenStreamRepository;
  private final StreamEventPublisher streamEventPublisher;

  /**
   * Constructs an {@code EventServiceImpl} with the specified dependencies.
   *
   * <p>This constructor initializes the event service with all necessary components to manage
   * event-related operations, including stream handling, attendee management, notifications,
   * and event updates. It inherits functionality from its parent class for stream and attendee
   * management, while adding specialized event-related services.</p>
   *
   * @param delegatedAuthorityEmail email address used for delegated authority in Google services.
   * @param countryService provides country-specific operations and data.
   * @param miscService handles miscellaneous utility operations.
   * @param eventUpdateService service for handling event-related updates.
   * @param notificationMessageService provides services for generating notification messages.
   * @param notificationService manages sending notifications.
   * @param chatSpaceMemberRepository repository for managing chat space members.
   * @param fleenStreamRepository repository for stream-related entities.
   * @param memberRepository repository for managing member entities.
   * @param userFleenStreamRepository repository for managing user-stream relationships.
   * @param streamAttendeeRepository repository for managing attendees of streams.
   * @param localizedResponse provides localized responses for API operations.
   * @param streamEventPublisher publishes events related to streams.
   * @param streamMapper maps stream-related entities and responses.
   * @param attendeeMapper maps stream attendee entities to response models.
   * @param commonMapper provides common mapping functionality for entities.
   */
  public EventServiceImpl(
      @Value("${google.delegated.authority.email}") final String delegatedAuthorityEmail,
      final CountryService countryService,
      final MiscService miscService,
      final EventUpdateService eventUpdateService,
      final NotificationMessageService notificationMessageService,
      final NotificationService notificationService,
      final ChatSpaceMemberRepository chatSpaceMemberRepository,
      final FleenStreamRepository fleenStreamRepository,
      final MemberRepository memberRepository,
      final UserFleenStreamRepository userFleenStreamRepository,
      final StreamAttendeeRepository streamAttendeeRepository,
      final LocalizedResponse localizedResponse,
      final StreamEventPublisher streamEventPublisher,
      final FleenStreamMapper streamMapper,
      final StreamAttendeeMapper attendeeMapper,
      final CommonMapper commonMapper) {
    super(miscService, fleenStreamRepository, streamAttendeeRepository, localizedResponse, streamMapper, attendeeMapper, commonMapper);
    this.delegatedAuthorityEmail = delegatedAuthorityEmail;
    this.eventUpdateService = eventUpdateService;
    this.notificationMessageService = notificationMessageService;
    this.notificationService = notificationService;
    this.chatSpaceMemberRepository = chatSpaceMemberRepository;
    this.memberRepository = memberRepository;
    this.userFleenStreamRepository = userFleenStreamRepository;
    this.streamEventPublisher = streamEventPublisher;
  }

  /**
   * Retrieves the necessary data required for creating an event, including available timezones.
   *
   * <p>This method fetches a set of available timezones and returns it wrapped in a
   * {@link DataForCreateEventResponse} object. The response is localized based on the current
   * user's locale, allowing the data to be displayed in a manner suitable for the user's region.</p>
   *
   * @return a {@link DataForCreateEventResponse} object containing the available timezones.
   */
  @Override
  public DataForCreateEventResponse getDataForCreateEvent() {
    // Get the set of available timezones.
    final Set<String> timezones = getAvailableTimezones();
    // Return the response object containing both the countries and timezones.
    return localizedResponse.of(DataForCreateEventResponse.of(timezones));
  }

  /**
   * Finds events or streams based on the search request and user context, then returns a search result view.
   * This method performs the following tasks:
   *
   * <p>Finds events or streams matching the search criteria provided in the search request.
   * Retrieves the event or stream IDs from the search results.
   * Determines the user's join status for each event or stream based on their attendance records.
   * Sets attendee details and total attendee count for each event or stream.
   * Fetches the first 10 attendees for each event or stream in any order.
   * Converts the results to a SearchResultView, including pagination details.</p>
   *
   * @param searchRequest The search request containing the filters and criteria for finding events or streams.
   * @param user The user performing the search, used to determine the user's join status for the events or streams.
   * @return EventSearchResult The result of the search, including event or stream details and pagination information.
   */
  @Override
  public EventSearchResult findEvents(final EventSearchRequest searchRequest, final FleenUser user) {
    // Find events or streams based on the search request
    final PageAndFleenStreamResponse pageAndFleenStreamResponse = findEventsOrStreams(searchRequest);
    // Get the list of event or stream views from the search result
    final List<FleenStreamResponse> views = pageAndFleenStreamResponse.getResponses();
    // Determine statuses like schedule, join status, schedules and timezones
    determineDifferentStatusesAndDetailsOfEventOrStreamBasedOnUser(views, user);
    // Set the attendees and total attendee count for each event or stream
    setStreamAttendeesAndTotalAttendeesAttending(views);
    // Get the first 10 attendees for each event or stream
    getFirst10AttendingInAnyOrder(views);
    // Return a search result view with the events responses and pagination details
    return handleSearchResult(
      pageAndFleenStreamResponse.getPage(),
      localizedResponse.of(EventSearchResult.of(toSearchResult(views, pageAndFleenStreamResponse.getPage()))),
      localizedResponse.of(EmptyEventSearchResult.of(toSearchResult(List.of(), pageAndFleenStreamResponse.getPage())))
    );
  }

  /**
   * Finds events based on the search criteria provided in the CalendarEventSearchRequest and the specified StreamTimeType.
   *
   * <p>This method retrieves events from the repository based on whether they are upcoming, past, or live events,
   * as determined by the StreamTimeType. It then converts the events to FleenStreamResponse views and returns a SearchResultView.</p>
   *
   * @param searchRequest the search request containing search criteria
   * @param streamTimeType the type of events to retrieve (upcoming, past, or live)
   * @return a EventSearchResult containing the events matching the search criteria
   */
  @Override
  public EventSearchResult findEvents(final EventSearchRequest searchRequest, final StreamTimeType streamTimeType) {
    final Page<FleenStream> page;

    // Determine the appropriate page of events based on the stream time type
    if (StreamTimeType.isUpcoming(streamTimeType)) {
      page = getUpcomingEvents(searchRequest);
    } else if (StreamTimeType.isPast(streamTimeType)) {
      page = getPastEvents(searchRequest);
    } else {
      page = getLiveEvents(searchRequest);
    }

    // Convert the page content to FleenStreamResponse objects
    final List<FleenStreamResponse> views = streamMapper.toFleenStreamResponses(page.getContent());
    // Set attendees and their total count for the retrieved events
    setStreamAttendeesAndTotalAttendeesAttending(views);
    // Retrieve the first 10 attendees for display purposes
    getFirst10AttendingInAnyOrder(views);
    // Return a search result view with the events responses and pagination details
    return handleSearchResult(
      page,
      localizedResponse.of(EventSearchResult.of(toSearchResult(views, page))),
      localizedResponse.of(EmptyEventSearchResult.of(toSearchResult(List.of(), page)))
    );
  }

  /**
   * Retrieves a page of upcoming events based on the search criteria provided in the CalendarEventSearchRequest.
   *
   * <p>If a query string is provided in the search request, the method searches for upcoming events with titles matching the query.
   * Otherwise, it retrieves all upcoming events.</p>
   *
   * @param searchRequest the search request containing search criteria
   * @return a page of upcoming FleenStream events
   */
  private Page<FleenStream> getUpcomingEvents(final EventSearchRequest searchRequest) {
    if (nonNull(searchRequest.getQ())) {
      return fleenStreamRepository.findUpcomingEventsByTitle(searchRequest.getQ(), LocalDateTime.now(), searchRequest.getPage());
    }
    return fleenStreamRepository.findUpcomingEvents(LocalDateTime.now(), searchRequest.getPage());
  }

  /**
   * Retrieves a page of past events based on the search criteria provided in the CalendarEventSearchRequest.
   *
   * <p>If a query string is provided in the search request, the method searches for past events with titles matching the query.
   * Otherwise, it retrieves all past events.</p>
   *
   * @param searchRequest the search request containing search criteria
   * @return a page of past FleenStream events
   */
  private Page<FleenStream> getPastEvents(final EventSearchRequest searchRequest) {
    if (nonNull(searchRequest.getQ())) {
      return fleenStreamRepository.findPastEventsByTitle(searchRequest.getQ(), LocalDateTime.now(), searchRequest.getPage());
    }
    return fleenStreamRepository.findPastEvents(LocalDateTime.now(), searchRequest.getPage());
  }

  /**
   * Retrieves a page of live events based on the search criteria provided in the CalendarEventSearchRequest.
   *
   * <p>If a query string is provided in the search request, the method searches for live events with titles matching the query.
   * Otherwise, it retrieves all live events.</p>
   *
   * @param searchRequest the search request containing search criteria
   * @return a page of live FleenStream events
   */
  private Page<FleenStream> getLiveEvents(final EventSearchRequest searchRequest) {
    if (nonNull(searchRequest.getQ())) {
      return fleenStreamRepository.findLiveEventsByTitle(searchRequest.getQ(), LocalDateTime.now(), searchRequest.getPage());
    }
    return fleenStreamRepository.findLiveEvents(LocalDateTime.now(), searchRequest.getPage());
  }

  /**
   * Finds events based on the provided search criteria and user.
   *
   * <p>This method searches for events that match the specified criteria such as date range, title, and visibility.
   * It returns a paginated result of events that the user has access to.</p>
   *
   * <p>The search criteria include start and end dates, event title, and stream visibility.
   * The method handles different combinations of these criteria to find the relevant events.</p>
   *
   * @param searchRequest the request object containing the search criteria
   * @param user the user performing the search
   * @return a EventSearchResult of the search results containing the matching events
   */
  @Override
  public EventSearchResult findMyEvents(final EventSearchRequest searchRequest, final FleenUser user) {
    final Page<FleenStream> page;
    final StreamVisibility streamVisibility = searchRequest.getVisibility(PUBLIC);

    if (searchRequest.areAllDatesSet() && nonNull(searchRequest.getStreamVisibility())) {
      // Filter by date range and visibility, if both are set
      page = userFleenStreamRepository.findByDateBetweenAndUser(searchRequest.getStartDateTime(), searchRequest.getEndDateTime(),
          streamVisibility, user.toMember(), searchRequest.getPage());
    } else if (searchRequest.areAllDatesSet()) {
      // Filter by date range, if only dates are set
      page = userFleenStreamRepository.findByDateBetweenAndUser(searchRequest.getStartDateTime(), searchRequest.getEndDateTime(),
          user.toMember(), searchRequest.getPage());
    } else if (nonNull(searchRequest.getTitle()) && nonNull(searchRequest.getStreamVisibility())) {
      // Filter by title and visibility, if both are set
      page = userFleenStreamRepository.findByTitleAndUser(searchRequest.getTitle(), streamVisibility, user.toMember(), searchRequest.getPage());
    } else if (nonNull(searchRequest.getTitle())) {
      // Filter by title, if only the title is set
      page = userFleenStreamRepository.findByTitleAndUser(searchRequest.getTitle(), user.toMember(), searchRequest.getPage());
    } else {
      // Retrieve all events for the user, if no other filters apply
      page = userFleenStreamRepository.findManyByMe(user.toMember(), searchRequest.getPage());
    }

    // Convert the event streams to response views
    final List<FleenStreamResponse> views = streamMapper.toFleenStreamResponses(page.getContent());
    // Determine the various status of the events user
    determineUserJoinStatusForEventOrStream(views, user);
    // Set other schedule details if user timezone is different
    setOtherScheduleBasedOnUserTimezone(views, user);
    // Set the attendees and total number of attendees for each event
    setStreamAttendeesAndTotalAttendeesAttending(views);
    // Retrieve the first 10 attendees in any order
    getFirst10AttendingInAnyOrder(views);
    // Return a search result view with the event responses and pagination details
    return handleSearchResult(
      page,
      localizedResponse.of(EventSearchResult.of(toSearchResult(views, page))),
      localizedResponse.of(EmptyEventSearchResult.of(toSearchResult(List.of(), page)))
    );
  }

  /**
   * Finds events attended by a user based on the provided search request.
   *
   * <p>This method searches for events attended by the given user based on the search criteria specified in the request.
   * It retrieves events attended by the user within a date range, by title, or all attended events if no specific criteria are provided.</p>
   *
   * @param searchRequest the request containing search criteria such as date range and title
   * @param user the user whose attended events are to be found
   * @return a EventSearchResult containing the events that match the search criteria
   */
  @Override
  public EventSearchResult findEventsAttendedByUser(final EventSearchRequest searchRequest, final FleenUser user) {
    final Page<FleenStream> page;

    if (searchRequest.areAllDatesSet()) {
      // Filter by date range if both start and end dates are set
      page = userFleenStreamRepository.findAttendedByDateBetweenAndUser(searchRequest.getStartDateTime(), searchRequest.getEndDateTime(),
          user.toMember(), searchRequest.getPage());
    } else if (nonNull(searchRequest.getTitle())) {
      // Filter by title if the title is provided
      page = userFleenStreamRepository.findAttendedByTitleAndUser(searchRequest.getTitle(), user.toMember(), searchRequest.getPage());
    } else {
      // Retrieve all attended events if no other filters are applied
      page = userFleenStreamRepository.findAttendedByUser(user.toMember(), searchRequest.getPage());
    }

    // Convert the event streams to response views
    final List<FleenStreamResponse> views = streamMapper.toFleenStreamResponsesNoJoinStatus(page.getContent());
    // Set the attendees and total number of attendees for each event
    setStreamAttendeesAndTotalAttendeesAttending(views);
    // Retrieve the first 10 attendees in any order
    getFirst10AttendingInAnyOrder(views);
    // Return a search result view with the events responses and pagination details
    return handleSearchResult(
      page,
      localizedResponse.of(EventSearchResult.of(toSearchResult(views, page))),
      localizedResponse.of(EmptyEventSearchResult.of(toSearchResult(List.of(), page)))
    );
  }

  /**
   * Finds events attended with another user based on the search criteria provided.
   *
   * <p>If the search request specifies another user ID, the method queries the repository to
   * find events attended together by the current user and the specified user ID.</p>
   *
   * <p>If no specific user ID is provided in the search request, an empty page is returned.</p>
   *
   * @param searchRequest the search criteria containing another user ID, if specified
   * @param user the current FleenUser for whom events are being searched
   * @return a EventSearchResult containing the list of events attended with another user
   */
  @Override
  public EventSearchResult findEventsAttendedWithAnotherUser(final EventSearchRequest searchRequest, final FleenUser user) {
    final Page<FleenStream> page;

    if (nonNull(searchRequest.getAnotherUserId())) {
      // Retrieve events attended together by the current user and another user
      page = userFleenStreamRepository.findEventsAttendedTogether(user.toMember(), Member.of(searchRequest.getAnotherUserId()), searchRequest.getPage());
    } else {
      // Return an empty result if anotherUserId is not provided
      page = new PageImpl<>(List.of());
    }

    // Convert the event streams to response views
    final List<FleenStreamResponse> views = streamMapper.toFleenStreamResponsesNoJoinStatus(page.getContent());
    // Set the attendees and total number of attendees for each event
    setStreamAttendeesAndTotalAttendeesAttending(views);
    // Retrieve the first 10 attendees in any order
    getFirst10AttendingInAnyOrder(views);
    // Return a search result view with the events responses and pagination details
    return handleSearchResult(
      page,
      localizedResponse.of(EventSearchResult.of(toSearchResult(views, page))),
      localizedResponse.of(EmptyEventSearchResult.of(toSearchResult(List.of(), page)))
    );
  }

  /**
   * Finds and retrieves attendees of a specific event based on the given event ID and search request.
   *
   * <p>This method queries the `streamAttendeeRepository` to fetch a paginated list of {@link StreamAttendee}
   * entities associated with the specified event ID. It then converts these entities into a list of
   * {@link StreamAttendeeResponse} objects and returns them as part of a {@link SearchResultView}.</p>
   *
   * @param eventId       the ID of the event whose attendees are to be found.
   * @param searchRequest the search request containing pagination and other search criteria.
   * @return a {@link StreamAttendeeSearchResult} containing the list of attendees and pagination details.
   */
  @Override
  public StreamAttendeeSearchResult findEventAttendees(final Long eventId, final StreamAttendeeSearchRequest searchRequest) {
    searchRequest.setPageSize(DEFAULT_NUMBER_OF_ATTENDEES_TO_GET_FOR_SEARCH);
    return findEventOrStreamAttendees(eventId, searchRequest);
  }

  /**
   * Retrieves details about a specific event, including its attendees and their statuses.
   *
   * @param eventId the ID of the event to retrieve
   * @param user the authenticated user
   * @return a {@link RetrieveStreamResponse} containing details of the event, its attendees, and the total count of approved attendees
   */
  @Override
  public RetrieveEventResponse retrieveEvent(final Long eventId, final FleenUser user) {
    final RetrieveEventOrStreamResponse response = retrieveEventOrStream(eventId, user);
    return localizedResponse.of(RetrieveEventResponse.of(eventId, response.getStream(), response.getAttendees(), response.getTotalAttending()));
  }

  /**
   * Converts a set of {@link StreamAttendee} objects into a set of {@link StreamAttendeeResponse} objects,
   * including each attendee's ID, full name, and request-to-join status.
   * This method filters non-null attendees and maps their relevant fields to a {@link StreamAttendeeResponse}.
   *
   * @param streamAttendees A set of {@link StreamAttendee} objects to be converted.
   *                        If null, an empty set will be returned.
   * @return A set of {@link StreamAttendeeResponse} objects containing the attendee's ID, full name,
   *         and request-to-join status. Returns an empty set if the input is null or empty.
   */
  protected Set<StreamAttendeeResponse> toStreamAttendeeResponsesWithStatus(final Set<StreamAttendee> streamAttendees, final FleenStream stream) {
    // Convert each StreamAttendee entity to StreamAttendeeResponse and include request-to-join status
    if (nonNull(streamAttendees)) {
      return streamAttendees.stream()
          .filter(Objects::nonNull)
          .map(attendee -> mapToStreamAttendeeResponse(attendee, stream))
          .collect(Collectors.toSet());
    }
    return Set.of();
  }

  /**
   * Maps a {@link StreamAttendee} object to a {@link StreamAttendeeResponse}.
   *
   * <p>This method extracts relevant information from the given StreamAttendee
   * and constructs a corresponding StreamAttendeeResponse object.</p>
   *
   * @param attendee The StreamAttendee object to be mapped.
   *
   * @return A StreamAttendeeResponse containing the mapped information.
   */
  protected StreamAttendeeResponse mapToStreamAttendeeResponse(final StreamAttendee attendee, final FleenStream stream) {
    // Extract the attendee's user ID and full name from the StreamAttendee object.
    final Long attendeeUserId = attendee.getMemberId();
    final String fullName = attendee.getFullName();

    // Create and return a new StreamAttendeeResponse object populated with the attendee's details.
    final StreamAttendeeResponse attendeeResponse = StreamAttendeeResponse.of(
      attendee.getStreamAttendeeId(),
      attendeeUserId,
      fullName
    );
    streamMapper.update(stream, attendee.getRequestToJoinStatus(), attendee.isAttending());
    return attendeeResponse;
  }

  /**
   * Converts a list of {@link StreamAttendee} objects into a list of {@link StreamAttendeeResponse} objects,
   * including each attendee's ID, full name, and request-to-join status.
   * The conversion removes duplicate attendees by transforming the list into a set before mapping.
   *
   * @param streamAttendees A list of {@link StreamAttendee} objects to be converted.
   *                        If null, an empty list will be returned.
   * @return A list of {@link StreamAttendeeResponse} objects containing the attendee's ID, full name,
   *         and request-to-join status. Duplicate entries in the input list are removed.
   */
  protected List<StreamAttendeeResponse> toStreamAttendeeResponsesWithStatus(final List<StreamAttendee> streamAttendees, final FleenStream stream) {
    final Set<StreamAttendee> streamAttendeesSet = new HashSet<>(streamAttendees);
    final Set<StreamAttendeeResponse> streamAttendeeResponses = toStreamAttendeeResponsesWithStatus(streamAttendeesSet, stream);
    return new ArrayList<>(streamAttendeeResponses);
  }

  /**
   * Creates a new event based on the information provided in the CreateCalendarEventDto and associated user details.
   *
   * <p>This method creates a new event by generating a CreateCalendarEventRequest from the provided DTO,
   * retrieves the appropriate calendar based on the user's country, updates the event request with
   * necessary details such as calendar ID, delegated authority email, and user email, creates the event
   * using an external service (Google Calendar), and finally saves the created event to the FleenStream repository.</p>
   *
   * @param createCalendarEventDto the DTO containing the details for creating the event
   * @param user                   the user associated with the event creation
   * @return {@link CreateEventResponse} containing the details of the newly created event
   * @throws CalendarNotFoundException if the calendar for the user's country is not found
   */
  @Override
  @Transactional
  public CreateEventResponse createEvent(final CreateCalendarEventDto createCalendarEventDto, final FleenUser user) {
    // Find the calendar associated with the user's country
    final Calendar calendar = findCalendar(user.getCountry());
    // Create a Calendar event request
    final CreateCalendarEventRequest createCalendarEventRequest = CreateCalendarEventRequest.by(createCalendarEventDto);

    // Set event organizer as attendee in Google calendar
    final String organizerAliasOrDisplayName = createCalendarEventDto.getOrganizerAlias(user.getFullName());
    final EventAttendeeOrGuest eventAttendeeOrGuest = EventAttendeeOrGuest.of(user.getEmailAddress(), organizerAliasOrDisplayName);

    // Add event organizer as an attendee
    createCalendarEventRequest.addAttendeeOrGuest(eventAttendeeOrGuest);
    // Update the event request with necessary details
    createCalendarEventRequest.update(calendar.getExternalId(), delegatedAuthorityEmail, user.getEmailAddress());

    // Create a FleenStream object from the DTO and update its details with the Google Calendar response
    FleenStream stream = createCalendarEventDto.toFleenStream(user.toMember());
    stream.updateDetails(
      organizerAliasOrDisplayName,
      user.getEmailAddress(),
      user.getPhoneNumber()
    );

    // Increase attendees count, save the event and and add the event in Google Calendar
    stream = increaseTotalAttendeesOrGuestsAndSaveBecauseOfOrganizer(stream);
    // Register the organizer of the event as an attendee or guest
    registerAndApproveOrganizerOfEventOrStreamAsAnAttendee(stream, user);
    // Create and add event in Calendar through external service
    eventUpdateService.createEventInGoogleCalendar(stream, createCalendarEventRequest);
    // Increment attendee count because of creator or organizer of event
    final FleenStreamResponse streamResponse = streamMapper.toFleenStreamResponseApproved(stream);
    // Return a localized response of the created event
    return localizedResponse.of(CreateEventResponse.of(stream.getStreamId(), streamResponse));
  }

  /**
   * Creates an instant event based on the information provided in the CreateInstantCalendarEventDto and associated user details.
   *
   * <p>This method creates an instant event by generating a CreateInstantCalendarEventRequest from the provided DTO,
   * retrieves the appropriate calendar based on the user's country, updates the event request with
   * necessary details such as calendar ID, creates the event using an external service (Google Calendar),
   * and finally saves the created event to the FleenStream repository.</p>
   *
   * @param createInstantCalendarEventDto the DTO containing the details for creating the instant event
   * @param user                          the user associated with the event creation
   * @return {@link CreateEventResponse} containing the details of the newly created instant event
   * @throws CalendarNotFoundException if the calendar for the user's country is not found
   */
  @Override
  @Transactional
  public CreateEventResponse createInstantEvent(final CreateInstantCalendarEventDto createInstantCalendarEventDto, final FleenUser user) {
    // Find the calendar associated with the user's country
    final Calendar calendar = findCalendar(user.getCountry());

    final CreateInstantCalendarEventRequest createInstantCalendarEventRequest = CreateInstantCalendarEventRequest.by(createInstantCalendarEventDto);
    // Update the instant event request with necessary details
    createInstantCalendarEventRequest.update(calendar.getExternalId());

    // Create a FleenStream object from the DTO and update its details with the Google Calendar response
    FleenStream stream = createInstantCalendarEventDto.toFleenStream(user.toMember());
    stream.updateDetails(
      user.getFullName(),
      user.getEmailAddress(),
      user.getPhoneNumber());

    // Increase attendees count, save the event and and add the event in Google Calendar
    stream = increaseTotalAttendeesOrGuestsAndSaveBecauseOfOrganizer(stream);
    // Register the organizer of the event as an attendee or guest
    registerAndApproveOrganizerOfEventOrStreamAsAnAttendee(stream, user);
    // Save stream and create event in Google Calendar Event Service externally
    stream = fleenStreamRepository.save(stream);
    // Create and add event in Calendar through external service
    eventUpdateService.createInstantEventInGoogleCalendar(stream, createInstantCalendarEventRequest);
    // Get the stream response
    final FleenStreamResponse streamResponse = streamMapper.toFleenStreamResponseApproved(stream);
    // Return a localized response of the created event
    return localizedResponse.of(CreateEventResponse.of(stream.getStreamId(), streamResponse));
  }

  /**
   * Updates an event identified by eventId with the information provided in the UpdateCalendarEventDto and associated user details.
   *
   * <p>This method updates an event by retrieving the appropriate calendar based on the user's country,
   * preparing a PatchCalendarEventRequest with the updated details, patching the event using an external service (Google Calendar),
   * and finally saving the updated event details to the FleenStream repository.</p>
   *
   * @param eventId                the ID of the event to update
   * @param updateCalendarEventDto the DTO containing the updated details for the event
   * @param user                   the user associated with the event update
   * @return a FleenStreamResponse containing the updated event details
   * @throws CalendarNotFoundException    if the calendar for the user's country is not found
   * @throws FleenStreamNotFoundException if the event with the specified ID is not found
   */
  @Override
  @Transactional
  public UpdateEventResponse updateEvent(final Long eventId, final UpdateCalendarEventDto updateCalendarEventDto, final FleenUser user) {
    // Find the calendar associated with the user's country
    final Calendar calendar = findCalendar(user.getCountry());
    // Find the stream by its ID
    FleenStream stream = findStream(eventId);

    // Validate if the user is the creator of the event
    verifyStreamDetails(stream, user);
    // Update the FleenStream object with the response from Google Calendar
    stream.update(
        updateCalendarEventDto.getTitle(),
        updateCalendarEventDto.getDescription(),
        updateCalendarEventDto.getTags(),
        updateCalendarEventDto.getLocation());

    stream = fleenStreamRepository.save(stream);

    // Prepare a request to patch the calendar event with updated details
    final PatchCalendarEventRequest patchCalendarEventRequest = PatchCalendarEventRequest.of(
      calendar.getExternalId(),
      stream.getExternalId(),
      updateCalendarEventDto.getTitle(),
      updateCalendarEventDto.getDescription(),
      updateCalendarEventDto.getLocation()
    );
    // Update the event details in the Google Calendar
    eventUpdateService.updateEventInGoogleCalendar(stream, patchCalendarEventRequest);
    // Get the stream response
    final FleenStreamResponse streamResponse = streamMapper.toFleenStreamResponseNoJoinStatus(stream);
    // Return a localized response the updated event
    return localizedResponse.of(UpdateEventResponse.of(stream.getStreamId(), streamResponse));
  }

  /**
   * Deletes an event identified by eventId associated with the provided user.
   *
   * <p>This method deletes an event by retrieving the appropriate calendar based on the user's country,
   * retrieving the event from the FleenStream repository, preparing a DeleteCalendarEventRequest,
   * deleting the event using an external service (Google Calendar), and logging the deletion.</p>
   *
   * @param eventId the ID of the event to delete
   * @param user    the user associated with the event deletion
   * @return a DeleteResponse indicating the successful deletion of the event
   * @throws CalendarNotFoundException    if the calendar for the user's country is not found
   * @throws FleenStreamNotFoundException if the event with the specified ID is not found
   */
  @Override
  @Transactional
  public DeletedEventResponse deleteEvent(final Long eventId, final FleenUser user) {
    // Find the calendar associated with the user's country
    final Calendar calendar = findCalendar(user.getCountry());
    // Find the stream by its ID
    final FleenStream stream = findStream(eventId);

    // Validate if the user is the creator of the event
    validateCreatorOfEvent(stream, user);
    // Verify if the event or stream is still ongoing
    checkIsTrue(stream.isOngoing(), CannotCancelOrDeleteOngoingStreamException.of(eventId));
    // Update delete status of event
    stream.delete();
    // Save the stream
    fleenStreamRepository.save(stream);

    // Create a request to delete the calendar event
    final DeleteCalendarEventRequest deleteCalendarEventRequest = DeleteCalendarEventRequest.of(
      calendar.getExternalId(),
      stream.getExternalId()
    );
    // Delete the event in the Google Calendar
    eventUpdateService.deleteEventInGoogleCalendar(deleteCalendarEventRequest);
    // Return a localized response of the Deleted event
    return localizedResponse.of(DeletedEventResponse.of(eventId));
  }

  /**
   * Cancels an event identified by eventId associated with the user.
   *
   * <p>This method cancels an event by retrieving the appropriate calendar based on the user's country,
   * finding the event in the FleenStream repository, validating if the user is the creator of the event,
   * creating a CancelCalendarEventRequest, cancelling the event using an external service (Google Calendar),
   * and logging the cancellation event. It returns a FleenFeenResponse confirming the cancellation of the event.</p>
   *
   * @param eventId the ID of the event to cancel
   * @param user    the user associated with the event cancellation
   * @return {@link CancelEventResponse} confirming the cancellation of the event
   * @throws CalendarNotFoundException    if the calendar for the user's country is not found
   * @throws FleenStreamNotFoundException if the event with the specified ID is not found
   */
  @Override
  @Transactional
  public CancelEventResponse cancelEvent(final Long eventId, final FleenUser user) {
    // Find the calendar associated with the user's country
    final Calendar calendar = findCalendar(user.getCountry());
    // Find the stream by its ID
    final FleenStream stream = findStream(eventId);

    // Verify stream details like the owner, event date and active status of the event
    verifyStreamDetails(stream, user);
    // Verify if the event or stream is still ongoing
    checkIsTrue(stream.isOngoing(), CannotCancelOrDeleteOngoingStreamException.of(eventId));
    // Update event status to canceled
    stream.cancel();
    // Save the stream to the repository
    fleenStreamRepository.save(stream);

    // Create a request to cancel the calendar event and submit request to external Calendar service
    final CancelCalendarEventRequest cancelCalendarEventRequest = CancelCalendarEventRequest.of(
      calendar.getExternalId(),
      stream.getExternalId()
    );
    // Cancel the stream in the external service
    eventUpdateService.cancelEventInGoogleCalendar(cancelCalendarEventRequest);
    // Convert the stream status to info
    final StreamStatusInfo statusInfo = streamMapper.toStreamStatus(stream);
    // Return a localized response of the cancellation
    return localizedResponse.of(CancelEventResponse.of(eventId, statusInfo));
  }

  /**
   * Marks the user as not attending the specified event.
   *
   * <p>This method updates the attendance status of a user for a given event. If the user is already registered
   * as attending the event, their status is changed to not attending. If the user is not registered, no changes
   * are made.</p>
   *
   * @param eventId the ID of the event for which the attendance status is to be updated
   * @param user the user whose attendance status is to be updated
   * @return a response indicating the result of the operation
   * @throws FleenStreamNotFoundException if the event with the specified ID is not found
   */
  @Override
  @Transactional
  public NotAttendingEventResponse notAttendingEvent(final Long eventId, final FleenUser user) {
    // Find the calendar associated with the user's country
    final Calendar calendar = findCalendar(user.getCountry());
    // Find the stream by its ID
    final FleenStream stream = findStream(eventId);
    // Verify if the user is the owner and fail the operation because the owner is automatically a member of the chat space
    verifyIfUserIsAuthorOrCreatorOrOwnerTryingToPerformAction(Member.of(stream.getMemberId()), user);

    // Find the existing attendee record for the user and event
    streamAttendeeRepository.findAttendeeByStreamAndUser(stream, user.toMember())
      .ifPresent(streamAttendee -> {
        // If an attendee record exists, update their attendance status to false
        streamAttendee.setIsNotAttending();
        // Decrease the total number of attendees to event
        decreaseTotalAttendeesOrGuestsAndSave(stream);
        // Save the updated attendee record
        streamAttendeeRepository.save(streamAttendee);
        // Create a request that remove the attendee from the Google Calendar event
        final NotAttendingEventRequest notAttendingEventRequest = NotAttendingEventRequest.of(calendar.getExternalId(), stream.getExternalId(), user.getEmailAddress());
        // Send the request for the update of non-attendance
        eventUpdateService.notAttendingEvent(notAttendingEventRequest);
    });
    // Return a localized response
    return localizedResponse.of(NotAttendingEventResponse.of());
  }

  /**
   * Reschedules an event identified by eventId with the new schedule details provided in the RescheduleCalendarEventDto and associated user details.
   *
   * <p>This method reschedules an event by retrieving the appropriate calendar based on the user's country,
   * finding the event in the FleenStream repository, validating if the user is the creator of the event,
   * preparing a RescheduleCalendarEventRequest with the new schedule details, rescheduling the event using an external service (Google Calendar),
   * updating the FleenStream repository with the new schedule, and logging the rescheduling event.</p>
   *
   * @param eventId                    the ID of the event to reschedule
   * @param rescheduleCalendarEventDto the DTO containing the new schedule details for the event
   * @param user                       the user associated with the event rescheduling
   * @return {@link RescheduleEventResponse} containing the rescheduled and updated event details
   * @throws CalendarNotFoundException            if the calendar for the user's country is not found
   * @throws FleenStreamNotFoundException         if the event with the specified ID is not found
   */
  @Override
  @Transactional
  public RescheduleEventResponse rescheduleEvent(final Long eventId, final RescheduleCalendarEventDto rescheduleCalendarEventDto, final FleenUser user) {
    // Find the calendar associated with the user's country
    final Calendar calendar = findCalendar(user.getCountry());
    // Find the stream by its ID
    final FleenStream stream = findStream(eventId);
    // Verify stream details like the owner, event date and active status of the event
    verifyStreamDetails(stream, user);

    // Prepare a request to reschedule the calendar event with the new schedule details
    final RescheduleCalendarEventRequest rescheduleCalendarEventRequest = RescheduleCalendarEventRequest.of(
      calendar.getExternalId(),
      stream.getExternalId(),
      rescheduleCalendarEventDto.getActualStartDateTime(),
      rescheduleCalendarEventDto.getActualStartDateTime(),
      rescheduleCalendarEventDto.getTimezone()
    );

    // Update Stream schedule details and time
    stream.updateSchedule(rescheduleCalendarEventDto.getActualStartDateTime(), rescheduleCalendarEventDto.getActualEndDateTime(), rescheduleCalendarEventDto.getTimezone());
    // Save the stream and event details
    fleenStreamRepository.save(stream);
    // Update event schedule details in the Google Calendar service
    eventUpdateService.rescheduleEventInGoogleCalendar(rescheduleCalendarEventRequest);
    // Get the stream response
    final FleenStreamResponse streamResponse = streamMapper.toFleenStreamResponseNoJoinStatus(stream);
    // Return a localized response of the rescheduled event
    return localizedResponse.of(RescheduleEventResponse.of(eventId, streamResponse));
  }

  /**
   * Allows a user to join an event identified by eventId.
   *
   * <p>This method allows a user to join an event by retrieving the appropriate calendar based on the user's country,
   * finding the event in the FleenStream repository, creating an AddNewEventAttendeeRequest, adding the user as an attendee
   * to the event using an external service (Google Calendar), logging the addition of the attendee, updating the FleenStream
   * repository with the new attendee, and returning the updated event details.</p>
   *
   * @param eventId the ID of the event to join
   * @param user the user who wants to join the event
   * @return an object containing the updated event details
   * @throws CalendarNotFoundException if the calendar for the user's country is not found
   * @throws FleenStreamNotFoundException if the event with the specified ID is not found
   */
  @Override
  @Transactional
  public JoinEventResponse joinEvent(final Long eventId, final JoinEventOrStreamDto joinEventOrStreamDto, final FleenUser user) {
    // Find the calendar associated with the user's country
    final Calendar calendar = findCalendar(user.getCountry());
    // Verify the user details and attempt to join the event
    final FleenStreamResponse streamResponse = joinEventOrStream(eventId, user);
    // Send invitation to new attendee
    createNewEventAttendeeRequestAndSendInvitation(calendar.getExternalId(), streamResponse.getExternalId(), user.getEmailAddress(), joinEventOrStreamDto.getComment());
    // Return localized response of the join event including status
    return localizedResponse.of(JoinEventResponse.of(eventId, streamResponse.getRequestToJoinStatusInfo(), streamResponse.getJoinStatusInfo()));
  }

  /**
   * Creates a new event attendee request and sends an invitation to the specified attendee.
   *
   * <p>This method constructs an {@code AddNewEventAttendeeRequest} using the provided
   * calendar ID, event or stream ID, attendee email address, and an optional comment.
   * The newly created request is then sent to the {@code eventUpdateService} to add
   * the attendee to the calendar event.</p>
   *
   * @param calendarExternalId The external ID of the calendar to which the event belongs.
   * @param eventOrStreamExternalId The external ID of the event or stream.
   * @param attendeeEmailAddress The email address of the attendee to invite.
   * @param comment An optional comment regarding the attendee invitation.
   */
  protected void createNewEventAttendeeRequestAndSendInvitation(final String calendarExternalId, final String eventOrStreamExternalId, final String attendeeEmailAddress, final String comment) {
    final AddNewEventAttendeeRequest addNewEventAttendeeRequest = AddNewEventAttendeeRequest
      .withComment(calendarExternalId,
        eventOrStreamExternalId,
        attendeeEmailAddress,
        comment);

    // Send an invitation to the user in the Calendar & Event API
    eventUpdateService.addNewAttendeeToCalendarEvent(addNewEventAttendeeRequest);
  }

  /**
   * Handles a request for a user to join an event or stream.
   *
   * <p>This method performs several checks and operations to process a user's request to join
   * a specific event or stream identified by the event ID. It verifies whether the user is eligible
   * to join, checks if the event is still active and not canceled, and ensures that the user is not
   * already an attendee. If the event is private, the user's join status will be set to pending.</p>
   *
   * <p>The method also handles cases where the event is linked to a chat space, ensuring
   * that the user is a member of that space and automatically approves their request if applicable.</p>
   *
   * @param eventId the ID of the event or stream the user is requesting to join
   * @param requestToJoinEventOrStreamDto the DTO containing the user's request details, such as a comment
   * @param user the {@link FleenUser} who is requesting to join the event or stream
   */
  @Override
  @Transactional
  public RequestToJoinEventResponse requestToJoinEvent(final Long eventId, final RequestToJoinEventOrStreamDto requestToJoinEventOrStreamDto, final FleenUser user) {
    // Find the stream by its ID
    final FleenStream stream = findStream(eventId);

    // Verify if the user is the owner and fail the operation because the owner is automatically a member of the chat space
    verifyIfUserIsAuthorOrCreatorOrOwnerTryingToPerformAction(Member.of(stream.getMemberId()), user);
    // Check if the stream is still active and can be joined.
    verifyStreamEndDate(stream);
    // Check if event is not cancelled
    verifyEventOrStreamIsNotCancelled(stream);
    // CHeck if the user is already an attendee
    checkIfUserIsAlreadyAnAttendeeAndThrowError(stream, user.getId());
    // Create a new StreamAttendee entry for the user
    final StreamAttendee streamAttendee = createStreamAttendeeWithComment(stream, user, requestToJoinEventOrStreamDto.getComment());
    // If the event is private, set the request to join status to pending
    setAttendeeRequestToJoinPendingIfStreamIsPrivate(streamAttendee, stream);
    // Check if the attendee is a member of a chat space if there is one associated with the event and approve their join request
    final boolean isMemberPartOfChatSpace = approveAttendeeRequestToJoinIfStreamHasChatSpaceAndAttendeeIsAMemberOfChatSpace(stream, streamAttendee);

    // Add the new StreamAttendee to the event's attendees list and save
    streamAttendeeRepository.save(streamAttendee);
    // Save the stream to the repository
    fleenStreamRepository.save(stream);
    // Create and save notification
    final Notification notification = notificationMessageService.ofReceived(stream, streamAttendee, stream.getMember(), user.toMember());
    // Save the notification
    notificationService.save(notification);

    // Verify if the attendee is a member of the chat space and send invitation
    checkIfAttendeeIsMemberOfChatSpaceAndSendInvitation(isMemberPartOfChatSpace, stream.getExternalId(), requestToJoinEventOrStreamDto.getComment(), user);
    // Get the join status info
    final JoinStatusInfo joinStatusInfo = streamMapper.toJoinStatus(streamMapper.toFleenStreamResponse(stream), streamAttendee.getRequestToJoinStatus(), streamAttendee.isAttending());
    // Get the request to join status info
    final StreamAttendeeRequestToJoinStatusInfo requestToJoinStatusInfo = streamMapper.toRequestToJoinStatus(streamAttendee.getRequestToJoinStatus());
    // Return the localized response of the request to join the event
    return localizedResponse.of(RequestToJoinEventResponse.of(eventId, requestToJoinStatusInfo, joinStatusInfo));
  }

  /**
   * Processes an attendee's request to join an event.
   *
   * <p>This method validates if the user is the creator of the event and if the event is still active.
   * It then processes the attendee's request to join the event, updating their status and adding them
   * as an attendee if the request is approved.</p>
   *
   * <p>If the request to join is approved, the attendee is added to the calendar event using an
   * external service (Google Calendar).</p>
   *
   * @param eventId                              the ID of the event the attendee wants to join
   * @param processAttendeeRequestToJoinEventOrStreamDto the DTO containing the details of the attendee's request
   * @param user                                 the user who is processing the request
   * @return {@link ProcessAttendeeRequestToJoinEventResponse} a response containing the result of processing the request
   * @throws FleenStreamNotFoundException if the event or stream cannot be found or does not exist
   * @throws CalendarNotFoundException if the calendar associated with event cannot be found or does not exist
   */
  @Override
  @Transactional
  public ProcessAttendeeRequestToJoinEventResponse processAttendeeRequestToJoinEvent(final Long eventId, final ProcessAttendeeRequestToJoinEventOrStreamDto processAttendeeRequestToJoinEventOrStreamDto, final FleenUser user) {
    // Retrieve the event (FleenStream) using the event ID
    final FleenStream stream = findStream(eventId);
    // Verify stream details like the owner, event date and active status of the event
    verifyStreamDetails(stream, user);

    // Check if the user is already an attendee and process accordingly
    checkIfUserIsAlreadyAnAttendee(stream, Long.parseLong(processAttendeeRequestToJoinEventOrStreamDto.getAttendeeUserId()))
      .ifPresentOrElse(
        streamAttendee -> processAttendeeRequestToJoin(stream, streamAttendee, processAttendeeRequestToJoinEventOrStreamDto, user),
        () -> {}
      );

    // Check if the user is already an attendee of the stream and process accordingly
    final Optional<StreamAttendee> existingAttendee = checkIfUserIsAlreadyAnAttendee(stream, Long.parseLong(processAttendeeRequestToJoinEventOrStreamDto.getAttendeeUserId()));
    // Process the request to join the event if the attendee exists
    existingAttendee.ifPresent(streamAttendee -> processAttendeeRequestToJoin(stream, streamAttendee, processAttendeeRequestToJoinEventOrStreamDto, user));
    // Get a processed attendee request to join event response
    final ProcessAttendeeRequestToJoinEventResponse processedRequestToJoin = commonMapper.processAttendeeRequestToJoinEvent(streamMapper.toFleenStreamResponse(stream), existingAttendee);
    // Return a localized response with the processed stream details
    return localizedResponse.of(processedRequestToJoin);
  }

  /**
   * Processes the attendee's request to join a event or stream.
   *
   * <p>This method checks whether the request is still pending. If the request is pending, it updates the request status
   * and adds any organizer comments. If the request is approved, it handles the necessary steps to invite the attendee
   * to the associated calendar event by calling {@code handleApprovedRequest}.</p>
   *
   * @param stream The event or stream the attendee has requested to join.
   * @param attendee The stream attendee whose request is being processed.
   * @param processRequestToJoinDto DTO containing the actual join status and optional organizer comment.
   * @param user The user processing the request.
   */
  protected void processAttendeeRequestToJoin(final FleenStream stream, final StreamAttendee attendee, final ProcessAttendeeRequestToJoinEventOrStreamDto processRequestToJoinDto,
      final FleenUser user) {
    // Check if the attendee's request is still pending
    if (!attendee.isRequestToJoinPending()) {
      return;
    }

    // Get the requested status for joining the event
    final StreamAttendeeRequestToJoinStatus joinStatus = processRequestToJoinDto.getActualJoinStatus();
    // Update the attendee's request status and set any organizer comments
    attendee.updateRequestStatusAndSetOrganizerComment(joinStatus, processRequestToJoinDto.getComment());
    // If the request is approved, proceed with handling the calendar invitation
    checkIfRequestToJoinApproveDAndHandleApproval(processRequestToJoinDto.isApproved(), stream, attendee, user);

    // Create and save notification
    final Notification notification = notificationMessageService.ofApprovedOrDisapproved(stream, attendee, stream.getMember());
    notificationService.save(notification);
  }

  /**
   * Checks if the request to join has been approved and handles the approval process.
   *
   * <p>This method verifies if the request to join a event or stream has been approved. If the request is approved,
   * it proceeds to handle the approval by adding the attendee to the event through {@code handleApprovedRequest}.</p>
   *
   * @param isApproved Indicates whether the request to join has been approved.
   * @param stream The event or stream that the attendee is requesting to join.
   * @param attendee The stream attendee whose request is being evaluated.
   * @param user The user responsible for handling the request.
   */
  protected void checkIfRequestToJoinApproveDAndHandleApproval(final boolean isApproved, final FleenStream stream, final StreamAttendee attendee, final FleenUser user) {
    if (isApproved) {
      // If the request is approved, proceed with handling the approval
      handleApprovedRequest(stream, attendee, user);
    }
  }

  /**
   * Handles the process of approving a stream attendee's request and adding them to the associated event.
   *
   * <p>This method saves the attendee information in the repository and adds the attendee to the calendar event linked to the stream.
   * It retrieves the calendar based on the user's country, saves the attendee details, and then sends an invitation to the event using
   * the attendee's email address.</p>
   *
   * @param stream The stream to which the attendee has been approved to join.
   * @param attendee The attendee whose request has been approved.
   * @param user The user approving the request, used to retrieve the relevant calendar.
   */
  protected void handleApprovedRequest(final FleenStream stream, final StreamAttendee attendee, final FleenUser user) {
    // Retrieve the calendar based on the user's country
    final Calendar calendar = findCalendar(user.getCountry());
    // Save the attendee details in the repository
    streamAttendeeRepository.save(attendee);
    // Add the attendee to the event associated with the stream
    addAttendeeToEvent(calendar.getExternalId(), stream.getExternalId(), attendee.getEmailAddress(), null);
  }

  /**
   * Updates the visibility of an event.
   *
   * <p>This method updates the visibility of a specified event for a given user.
   * It ensures that the user is the creator of the event, the event is not past its end date,
   * is not cancelled, and is still scheduled or ongoing.</p>
   *
   * <p>The method first retrieves the calendar associated with the user's country.
   * If the calendar is not found, it throws a CalendarNotFoundException.
   * Then it retrieves the event (FleenStream) by its ID. If the event is not found,
   * it throws a FleenStreamNotFoundException. The method validates that the user is the
   * creator of the event, verifies the event's end date, checks that the event is not
   * cancelled, and ensures the event is either scheduled or ongoing.</p>
   *
   * <p>After performing the necessary checks, the method creates a request to update
   * the event's visibility and sends it to the Google Calendar service. It updates the
   * event's visibility in the repository and returns a FleenFeenResponse with the event ID.</p>
   *
   * @param eventId                  the ID of the event to update
   * @param updateEventOrStreamVisibilityDto the DTO containing the new visibility status
   * @param user                     the user requesting the update
   * @return {@link UpdateEventVisibilityResponse} containing the event and event details
   * @throws CalendarNotFoundException if the calendar associated with event cannot be found or does not exist
   * @throws FleenStreamNotFoundException if the event or stream cannot be found or does not exist
   */
  @Override
  @Transactional
  public UpdateEventVisibilityResponse updateEventVisibility(final Long eventId, final UpdateEventOrStreamVisibilityDto updateEventOrStreamVisibilityDto, final FleenUser user) {
    // Find the calendar associated with the user's country
    final Calendar calendar = findCalendar(user.getCountry());
    // Find the stream by its ID
    final FleenStream stream = findStream(eventId);
    // Retrieve the current or existing status or visibility status of a stream
    final StreamVisibility currentStreamVisibility = stream.getStreamVisibility();
    // Verify stream details like the owner, event date and active status of the event
    verifyStreamDetails(stream, user);
    // Verify if the event or stream is still ongoing
    checkIsTrue(stream.isOngoing(), CannotCancelOrDeleteOngoingStreamException.of(eventId));
    // Update the visibility of an event or stream
    updateStreamVisibility(stream, updateEventOrStreamVisibilityDto.getActualVisibility());
    // Create a request to update the event's visibility and initiate update
    updateCalendarEventVisibility(calendar, stream, updateEventOrStreamVisibilityDto);
    // Send invitation to attendees that requested to join earlier and whose request is pending because the event or stream was private earlier
    sendInvitationToPendingAttendeesBasedOnCurrentStreamStatus(calendar.getExternalId(), stream, currentStreamVisibility);

    return localizedResponse.of(UpdateEventVisibilityResponse.of(eventId, streamMapper.toFleenStreamResponseNoJoinStatus(stream)));
  }

  /**
   * Updates the visibility of the given stream.
   *
   * <p>This method sets a new visibility status for the provided {@code FleenStream}. Once the visibility is updated,
   * the stream's state is saved in the repository to persist the change.</p>
   *
   * @param stream The stream whose visibility is being updated.
   * @param newVisibility The new visibility status to apply to the stream.
   */
  protected void updateStreamVisibility(final FleenStream stream, final StreamVisibility newVisibility) {
    // Update the visibility of an event or stream
    stream.setStreamVisibility(newVisibility);
    // Save the updated stream in the repository
    fleenStreamRepository.save(stream);
  }

  /**
   * Updates the visibility of a calendar event associated with a stream.
   *
   * <p>This method updates the visibility of a Google Calendar event for a specific stream. It creates a request to modify
   * the visibility settings based on the provided {@code UpdateEventOrStreamVisibilityDto} and then sends this request
   * to the event update service.</p>
   *
   * @param calendar The calendar where the event is hosted.
   * @param stream The stream associated with the event whose visibility is being updated.
   * @param dto The DTO containing the visibility update information.
   */
  protected void updateCalendarEventVisibility(final Calendar calendar, final FleenStream stream, final UpdateEventOrStreamVisibilityDto dto) {
    // Create a request to update the Calendar event's visibility
    final UpdateCalendarEventVisibilityRequest request = UpdateCalendarEventVisibilityRequest.of(
      calendar.getExternalId(),
      stream.getExternalId(),
      dto.getVisibility()
    );
    // Update the event visibility on the Google Calendar Service
    eventUpdateService.updateEventVisibility(request);
  }

  /**
   * Sends invitations to pending attendees based on the current stream status and previous visibility.
   *
   * <p>This method checks if the provided FleenStream is null. If so, it throws an FailedOperationException.</p>
   *
   * <p>It then compares the current stream visibility with the previous visibility.
   * If the current visibility is PUBLIC and the previous visibility was PRIVATE or PROTECTED,
   * it retrieves all pending StreamAttendees associated with the stream.The email addresses of these attendees are collected into a set.</p>
   *
   * <p>An AddCalendarEventAttendeesEvent is created and published using streamEventPublisher to add the new attendees
   * to the specified calendar and stream.</p>
   *
   * @param calendarExternalId the external identifier of the calendar where the event resides
   * @param stream the FleenStream object representing the event
   * @param previousStreamVisibility the previous visibility status of the stream
   * @throws FailedOperationException if the provided FleenStream object is null
   */
  public void sendInvitationToPendingAttendeesBasedOnCurrentStreamStatus(final String calendarExternalId, final FleenStream stream, final StreamVisibility previousStreamVisibility) {
    // Throw an exception if the any of the provided values is null
    checkIsNullAny(Set.of(stream, previousStreamVisibility), FailedOperationException::new);
    // Determine the updated or current visibility of the stream
    final StreamVisibility currentStreamVisibility = stream.getStreamVisibility();

    // If the stream visibility is PUBLIC, and it was previously PRIVATE or PROTECTED
    if (StreamVisibility.isPublic(currentStreamVisibility) && StreamVisibility.isPrivateOrProtected(previousStreamVisibility)) {
      // Process pending attendees and send invitations
      processPendingAttendees(calendarExternalId, stream);
    }
  }

  /**
   * Processes all pending attendee requests for a specific stream and approves them.
   *
   * <p>This method retrieves all attendees with a pending request to join the specified stream.
   * It then approves their requests and adds them to the corresponding calendar event using
   * their email addresses.</p>
   *
   * @param calendarExternalId The external ID of the calendar associated with the stream.
   * @param stream The stream for which pending attendees are being processed.
   */
  protected void processPendingAttendees(final String calendarExternalId, final FleenStream stream) {
    // Retrieve all pending attendees for the specified stream
    final List<StreamAttendee> pendingAttendees = streamAttendeeRepository.findAllByFleenStreamAndRequestToJoinStatus(stream, PENDING);

    // Extract email addresses and IDs of the pending attendees or guests
    final Set<String> attendeesOrGuestsEmailAddresses = getAttendeesEmailAddresses(pendingAttendees);
    final Set<Long> attendeeIds = getAttendeeIds(pendingAttendees);

    // Approve all pending requests
    streamAttendeeRepository.approveAllAttendeeRequestInvitation(APPROVED, new ArrayList<>(attendeeIds));
    // Add attendees to the calendar event
    addNewAttendeesToCalendar(calendarExternalId, stream, attendeesOrGuestsEmailAddresses);
  }

  /**
   * Adds new attendees to a calendar event associated with a specified stream.
   *
   * <p>This method creates an event to add attendees to a calendar based on their email addresses
   * and publishes it through the event publisher.</p>
   *
   * @param calendarExternalId The external ID of the calendar to which attendees are being added.
   * @param stream The stream associated with the calendar event.
   * @param attendeesOrGuestsEmailAddresses A set of email addresses of the attendees or guests to be added.
   */
  protected void addNewAttendeesToCalendar(final String calendarExternalId, final FleenStream stream, final Set<String> attendeesOrGuestsEmailAddresses) {
    // Create an event object containing the details of the attendees to be added
    final AddCalendarEventAttendeesEvent addAttendeesEvent = AddCalendarEventAttendeesEvent.of(
      calendarExternalId,
      stream.getExternalId(),
      attendeesOrGuestsEmailAddresses,
      Set.of()
    );
    // Publish the event to add the new attendees to the calendar
    streamEventPublisher.addNewAttendees(addAttendeesEvent);
  }

  /**
   * Retrieves the set of attendee IDs from a list of StreamAttendee objects.
   *
   * @param attendees List of StreamAttendees from which the IDs will be extracted.
   * @return A set of attendee IDs.
   */
  protected Set<Long> getAttendeeIds(final List<StreamAttendee> attendees) {
    // Stream over the list of StreamAttendees
    if (nonNull(attendees)) {
      // Map each StreamAttendee to its StreamAttendeeId and collect the IDs into a set
      return attendees.stream()
        .map(StreamAttendee::getStreamAttendeeId)
        .collect(Collectors.toSet());
    }
    return Set.of();
  }

  /**
   * Retrieves email addresses of attendees from a list of StreamAttendee objects.
   *
   * <p>This method filters out null StreamAttendee objects, retrieves the email addresses of the
   * associated members, and collects them into a set of strings.</p>
   *
   * @param streamAttendees the list of StreamAttendee objects
   * @return a set of email addresses of attendees, or an empty set if streamAttendees is null
   */
  public Set<String> getAttendeesEmailAddresses(final List<StreamAttendee> streamAttendees) {
    if (nonNull(streamAttendees)) {
      return streamAttendees.stream()
          .filter(Objects::nonNull)
          .map(StreamAttendee::getEmailAddress)
          .collect(Collectors.toSet());
    }
    return Collections.emptySet();
  }

  /**
   * Adds a new attendee to an event.
   *
   * <p>This method adds a new attendee to an event identified by eventId. It verifies the stream details,
   * checks if the attendee already exists, updates the attendee's status if they are pending, and adds the attendee
   * to the event using an external service.</p>
   *
   * @param eventId                the unique identifier of the event
   * @param addNewEventAttendeeDto the DTO containing the details of the new attendee
   * @param user                   the FleenUser performing the action
   * @return AddNewEventAttendeeResponse the response containing the updated event, added attendee and details
   * @throws CalendarNotFoundException    if the calendar associated with the user's country is not found
   * @throws FleenStreamNotFoundException if the event with the specified eventId is not found
   */
  @Override
  @Transactional
  public AddNewEventAttendeeResponse addEventAttendee(final Long eventId, final AddNewEventAttendeeDto addNewEventAttendeeDto, final FleenUser user) {
    // Find the calendar associated with the user's country
    final Calendar calendar = findCalendar(user.getCountry());
    // Find the stream by its ID
    final FleenStream stream = findStream(eventId);

    // Verify stream details like the owner, event date and active status of the event
    verifyStreamDetails(stream, user);
    // Handle the request to add the attendee to the event
    handleAttendeeRequest(calendar, stream, addNewEventAttendeeDto);

    // Save stream to the repository
    fleenStreamRepository.save(stream);
    // Return a localized response with the details of the added attendee
    return localizedResponse.of(AddNewEventAttendeeResponse.of(eventId, streamMapper.toFleenStreamResponseNoJoinStatus(stream), addNewEventAttendeeDto.getEmailAddress()));
  }

  /**
   * Handles a request to add a new attendee to a stream by checking if the attendee already exists.
   *
   * <p>This method searches for an existing attendee based on the provided email address. If an attendee
   * is found, it processes the existing attendee's request. If no attendee is found, it approves the
   * new attendee and sends an invitation to join the stream.</p>
   *
   * @param calendar The calendar associated with the stream where the attendee is to be added.
   * @param stream The stream for which the attendee request is being handled.
   * @param addNewEventAttendeeDto The DTO containing the information of the attendee to be added.
   */
  protected void handleAttendeeRequest(final Calendar calendar, final FleenStream stream, final AddNewEventAttendeeDto addNewEventAttendeeDto) {
    // Find an existing stream attendee by their email address
    streamAttendeeRepository.findDistinctByEmail(addNewEventAttendeeDto.getEmailAddress())
      .ifPresentOrElse(
        // If an existing attendee is found, process their request
        streamAttendee -> processExistingAttendee(calendar, stream, streamAttendee, addNewEventAttendeeDto),
        // If no existing attendee is found, approve and add the new attendee to the stream
        () -> approveAndAddAttendeeToStreamAttendeesAndSendInvitation(calendar, stream, addNewEventAttendeeDto)
      );
  }

  /**
   * Processes an existing attendee for the specified stream by approving their request and adding them to the event.
   *
   * <p>This method checks if the request status of the existing attendee is either pending or disapproved.
   * If so, it approves the attendee's request, sets the organizer's comment, and adds the attendee to the
   * event using the external service. Finally, it saves the updated attendee information to the repository.</p>
   *
   * @param calendar The calendar associated with the stream where the attendee is to be added.
   * @param stream The stream that the attendee is associated with.
   * @param streamAttendee The existing stream attendee whose request is being processed.
   * @param addNewEventAttendeeDto The DTO containing the information of the attendee to be added.
   */
  protected void processExistingAttendee(final Calendar calendar, final FleenStream stream, final StreamAttendee streamAttendee,
      final AddNewEventAttendeeDto addNewEventAttendeeDto) {
      // Check if the stream attendee's request is pending or disapproved
    if (isStreamAttendeeRequestPendingOrDisapproved(streamAttendee)) {
      // Approve the attendee request and set the organizer comment
      approveAttendeeRequestAndSetOrganizerComment(streamAttendee, addNewEventAttendeeDto.getComment());
      // Add the attendee to the event using the external service
      addAttendeeToEvent(calendar.getExternalId(), stream.getExternalId(), addNewEventAttendeeDto.getEmailAddress(), addNewEventAttendeeDto.getAliasOrDisplayName());
      // Save the updated stream attendee information to the repository
      streamAttendeeRepository.save(streamAttendee);
    }
  }

  /**
   * Approves an attendee's request and adds the attendee to the specified stream.
   *
   * <p>This method checks if a member with the given email address already exists. If the member exists,
   * it creates a new {@link StreamAttendee} for the given {@link FleenStream} and approves the attendee's
   * request by setting the request status to {@code APPROVED} and adding the organizer's comment. The
   * new attendee is then saved to the repository.</p>
   *
   * @param stream the {@link FleenStream} to which the attendee is to be added.
   * @param addNewEventAttendeeDto the DTO containing the new attendee's email address and the organizer's comment.
   */
  protected void approveAndAddAttendeeToStreamAttendees(final FleenStream stream, final AddNewEventAttendeeDto addNewEventAttendeeDto) {
    // Check if the member with the given email address already exists in the repository
    memberRepository.findByEmailAddress(addNewEventAttendeeDto.getEmailAddress())
      .ifPresent(member -> {
        // If the member exists, proceed to create and approve the attendee
        final StreamAttendee streamAttendee = createStreamAttendee(stream, FleenUser.of(member.getMemberId()));
        approveAttendeeRequestAndSetOrganizerComment(streamAttendee, addNewEventAttendeeDto.getComment());
        streamAttendeeRepository.save(streamAttendee);
    });
  }

  /**
   * Approves the attendee and adds them to the stream's attendees, then sends an invitation.
   *
   * <p>This method first approves the attendee and adds them to the list of attendees for the specified
   * {@code FleenStream}. After the attendee is successfully added, it sends an invitation by adding them
   * to the corresponding event using an external service.</p>
   *
   * @param calendar The calendar associated with the stream where the attendee is to be added.
   * @param stream The stream to which the attendee is being added.
   * @param addNewEventAttendeeDto The DTO containing the information of the attendee to be added.
   */
  protected void approveAndAddAttendeeToStreamAttendeesAndSendInvitation(final Calendar calendar, final FleenStream stream, final AddNewEventAttendeeDto addNewEventAttendeeDto) {
    // Approve the attendee and add them to the stream's attendees list
    approveAndAddAttendeeToStreamAttendees(stream, addNewEventAttendeeDto);
    // Add attendee to the event using an external service
    addAttendeeToEvent(calendar.getExternalId(), stream.getExternalId(), addNewEventAttendeeDto.getEmailAddress(), addNewEventAttendeeDto.getAliasOrDisplayName());
  }

  /**
   * Retrieves a paginated list of attendee requests with a status of {@code PENDING} to join a specific event identified by its {@code eventId}.
   * The method first validates that the provided {@link FleenUser} is the creator of the event to ensure that only authorized users can access
   * the attendee requests.
   *
   * <p>After validation, it fetches the {@link StreamAttendee} objects with a status of {@code PENDING} associated with the event based on the
   * provided search criteria encapsulated in {@link StreamAttendeeSearchRequest}. The list of {@link StreamAttendee} objects is then converted
   * into a list of {@link StreamAttendeeResponse} objects and wrapped in a {@link SearchResultView}, which includes pagination information.</p>
   *
   * @param eventId The ID of the event for which attendee requests are being fetched.
   * @param searchRequest A {@link StreamAttendeeSearchRequest} object containing the search criteria and pagination details.
   * @param user The {@link FleenUser} object representing the user making the request, which is validated as the creator of the event.
   * @return A {@link RequestToJoinSearchResult} object containing a list of {@link StreamAttendeeResponse} objects with a status of {@code PENDING}
   *         and pagination information.
   * @throws FleenStreamNotFoundException if no event with the given {@code eventId} is found.
   */
  @Override
  public RequestToJoinSearchResult getEventAttendeeRequestsToJoinEvent(final Long eventId, final StreamAttendeeSearchRequest searchRequest, final FleenUser user) {
    // Find the stream by its ID
    final FleenStream stream = findStream(eventId);
    // Validate owner of the event
    validateCreatorOfEvent(stream, user);

    final Page<StreamAttendee> page = streamAttendeeRepository.findByFleenStreamAndRequestToJoinStatus(stream, PENDING, searchRequest.getPage());
    final List<StreamAttendeeResponse> views = toStreamAttendeeResponsesWithStatus(page.getContent(), stream);
    // Return a search result view with the attendee responses and pagination details
    return handleSearchResult(
      page,
      localizedResponse.of(RequestToJoinSearchResult.of(toSearchResult(views, page))),
      localizedResponse.of(EmptyRequestToJoinSearchResult.of(toSearchResult(List.of(), page)))
    );
  }

  /**
   * Checks if the request status of a given {@link StreamAttendee} is either pending or disapproved.
   *
   * <p>This method evaluates the request-to-join status of a {@link StreamAttendee} and returns true if the status
   * is either {@code PENDING} or {@code DISAPPROVED}. It can be used to determine if further action is needed
   * for the given attendee's request.</p>
   *
   * @param streamAttendee the {@link StreamAttendee} whose request status is to be checked.
   * @return {@code true} if the request status is {@code PENDING} or {@code DISAPPROVED}, {@code false} otherwise.
   */
  protected boolean isStreamAttendeeRequestPendingOrDisapproved(final StreamAttendee streamAttendee) {
    return streamAttendee.isRequestToJoinPending() || streamAttendee.isRequestToJoinDisapproved();
  }

  /**
   * Approves a stream attendee's request to join and sets the organizer's comment.
   *
   * <p>This method updates the request status of a given {@link StreamAttendee} to {@code APPROVED}
   * and sets the organizer's comment. It is used to indicate that the attendee's request has been
   * approved and to provide any additional comments from the organizer.</p>
   *
   * @param streamAttendee the {@link StreamAttendee} whose request status is to be updated.
   * @param comment the comment from the organizer to be set for the attendee.
   */
  protected void approveAttendeeRequestAndSetOrganizerComment(final StreamAttendee streamAttendee, final String comment) {
    if (nonNull(streamAttendee)) {
      streamAttendee.setRequestToJoinStatus(APPROVED);
      streamAttendee.setOrganizerComment(comment);
    }
  }

  /**
   * Retrieves the list of attendees for a given event identified by eventId and converts them to an EventAttendeesResponse DTO.
   *
   * <p>If the event with the specified eventId is not found in the database, a FleenStreamNotFoundException is thrown.</p>
   *
   * <p>The method fetches the event details from the database using the eventId and then delegates to getAttendees method
   * to convert the attendees into a structured response.</p>
   *
   * @param eventId the unique identifier of the event
   * @param user the authenticated user
   * @return an StreamAttendeeSearchResult containing the search result list of attendees for the event or stream
   * @throws FleenStreamNotFoundException if the event with the specified eventId is not found
   */
  @Override
  public StreamAttendeeSearchResult getEventAttendees(final Long eventId, final StreamAttendeeSearchRequest searchRequest, final FleenUser user) {
    searchRequest.setPageSize(DEFAULT_NUMBER_OF_ATTENDEES_TO_GET_FOR_SEARCH);

    final EventOrStreamAttendeesResponse eventOrStreamAttendeesResponse = getEventOrStreamAttendees(eventId, searchRequest, searchRequest.googleMeet());
    // Return a search result view with the attendees responses and pagination details
    return handleSearchResult(
      eventOrStreamAttendeesResponse.getPage(),
      localizedResponse.of(StreamAttendeeSearchResult.of(toSearchResult(new ArrayList<>(eventOrStreamAttendeesResponse.getAttendees()), eventOrStreamAttendeesResponse.getPage()))),
      localizedResponse.of(EmptyStreamAttendeeSearchResult.of(toSearchResult(List.of(), eventOrStreamAttendeesResponse.getPage())))
    );
  }

  /**
   * Counts the total number of events created by a user.
   *
   * <p>This method retrieves the total number of events created by a given user.
   * It uses the userFleenStreamRepository to count the total events and returns the count in a response object.</p>
   *
   * @param user the user whose events are to be counted
   * @return a response object containing the total number of events created by the user
   */
  @Override
  public TotalEventsCreatedByUserResponse countTotalEventsByUser(final FleenUser user) {
    final Long totalCount = userFleenStreamRepository.countTotalEventsByUser(user.toMember());
    return TotalEventsCreatedByUserResponse.of(totalCount);
  }

  /**
   * Counts the total number of events attended by a user.
   *
   * <p>This method retrieves the total number of events attended by a given user.
   * It uses the userFleenStreamRepository to count the total events attended and returns the count in a response object.</p>
   *
   * @param user the user whose attended events are to be counted
   * @return a response object containing the total number of events attended by the user
   */
  @Override
  public TotalEventsAttendedByUserResponse countTotalEventsAttended(final FleenUser user) {
    final Long totalCount = userFleenStreamRepository.countTotalEventsAttended(user.toMember());
    return TotalEventsAttendedByUserResponse.of(totalCount);
  }

  /**
   * Adds an attendee to an event in the specified calendar using the Google Calendar service.
   *
   * <p>This method constructs an {@link AddNewEventAttendeeRequest} using the provided calendar external ID,
   * stream external ID, and attendee email address. It then uses the Google Calendar service to add the
   * attendee to the event and logs the response.</p>
   *
   * @param calendarExternalId the external ID of the calendar
   * @param streamExternalId the external ID of the stream (event)
   * @param attendeeEmailAddress the email address of the attendee to be added
   * @param displayOrAliasName the alias to use for the attendee which is optional
   */
  public void addAttendeeToEvent(final String calendarExternalId, final String streamExternalId, final String attendeeEmailAddress, final String displayOrAliasName) {
    // Create a request to add the user as an attendee to the calendar event
    final AddNewEventAttendeeRequest addNewEventAttendeeRequest = AddNewEventAttendeeRequest.of(
      calendarExternalId,
      streamExternalId,
      attendeeEmailAddress,
      displayOrAliasName);

    eventUpdateService.addNewAttendeeToCalendarEvent(addNewEventAttendeeRequest);
  }

  /**
   * Checks if the attendee is a member of the chat space and sends an invitation if they are.
   *
   * <p>This method verifies whether the attendee is a member of the chat space. If the attendee
   * is a member, it calls {@code createNewEventAttendeeRequestAndSendInvitation} to create an
   * event attendee request and send an invitation to the specified attendee.</p>
   *
   * @param isAttendeeMemberOfChatSpace A boolean indicating if the attendee is a member of the chat space.
   * @param eventOrStreamExternalId The external ID of the event or stream.
   * @param comment An optional comment for the attendee invitation.
   * @param user The user to add as an event attendee
   */
  protected void checkIfAttendeeIsMemberOfChatSpaceAndSendInvitation(final boolean isAttendeeMemberOfChatSpace, final String eventOrStreamExternalId,
      final String comment, final FleenUser user) {
    if (isAttendeeMemberOfChatSpace) {
      // Find calendar associated with user's country
      final Calendar calendar = findCalendar(user.getCountry());
      // Create add event attendee to Calendar Event and send invitation
      createNewEventAttendeeRequestAndSendInvitation(calendar.getExternalId(), eventOrStreamExternalId, user.getEmailAddress(), comment);
    }
  }

  /**
   * Approves the attendee's request to join a stream if the stream is linked to a chat space and the attendee is a member of the chat space.
   *
   * <p>This method checks whether the provided stream has an associated chat space. If so, it verifies if the attendee is a member
   * of the chat space by searching for an existing chat space member. If the attendee is found to be a member, the method returns {@code true};
   * otherwise, it returns {@code false}.</p>
   *
   * @param stream The stream entity which may be associated with a chat space.
   * @param streamAttendee The attendee whose membership in the chat space is being evaluated.
   * @return {@code true} if the stream has a chat space and the attendee is a member of that chat space; {@code false} otherwise.
   */
  protected boolean approveAttendeeRequestToJoinIfStreamHasChatSpaceAndAttendeeIsAMemberOfChatSpace(final FleenStream stream, final StreamAttendee streamAttendee) {
    // Check if the stream has an associated chat space with a valid ID
    if (nonNull(stream.getChatSpace()) && nonNull(stream.getChatSpace().getChatSpaceId())) {
      // Find if the attendee is a member of the chat space
      final Optional<ChatSpaceMember> existingChatSpaceMember = chatSpaceMemberRepository.findByChatSpaceAndMember(stream.getChatSpace(), streamAttendee.getMember());
      // Return true if a member exists, otherwise false
      return existingChatSpaceMember.isPresent();
    }
    // Return false if there's no chat space or no valid chat space ID
    return false;
  }

}