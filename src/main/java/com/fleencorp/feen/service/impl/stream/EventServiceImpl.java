package com.fleencorp.feen.service.impl.stream;

import com.fleencorp.base.model.view.search.SearchResultView;
import com.fleencorp.feen.constant.stream.StreamAttendeeRequestToJoinStatus;
import com.fleencorp.feen.constant.stream.StreamTimeType;
import com.fleencorp.feen.constant.stream.StreamVisibility;
import com.fleencorp.feen.event.publisher.StreamEventPublisher;
import com.fleencorp.feen.exception.calendar.CalendarNotFoundException;
import com.fleencorp.feen.exception.stream.CannotCancelOrDeleteOngoingStreamException;
import com.fleencorp.feen.exception.stream.FleenStreamNotFoundException;
import com.fleencorp.feen.exception.stream.UnableToCompleteOperationException;
import com.fleencorp.feen.model.domain.calendar.Calendar;
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
import com.fleencorp.feen.model.request.calendar.event.*;
import com.fleencorp.feen.model.request.search.calendar.CalendarEventSearchRequest;
import com.fleencorp.feen.model.request.search.stream.StreamAttendeeSearchRequest;
import com.fleencorp.feen.model.response.event.*;
import com.fleencorp.feen.model.response.stream.EventOrStreamAttendeesResponse;
import com.fleencorp.feen.model.response.stream.FleenStreamResponse;
import com.fleencorp.feen.model.response.stream.PageAndFleenStreamResponse;
import com.fleencorp.feen.model.response.stream.StreamAttendeeResponse;
import com.fleencorp.feen.model.security.FleenUser;
import com.fleencorp.feen.repository.calendar.CalendarRepository;
import com.fleencorp.feen.repository.stream.FleenStreamRepository;
import com.fleencorp.feen.repository.stream.StreamAttendeeRepository;
import com.fleencorp.feen.repository.stream.UserFleenStreamRepository;
import com.fleencorp.feen.repository.user.MemberRepository;
import com.fleencorp.feen.service.common.CountryService;
import com.fleencorp.feen.service.i18n.LocalizedResponse;
import com.fleencorp.feen.service.impl.stream.base.StreamService;
import com.fleencorp.feen.service.impl.stream.update.EventUpdateService;
import com.fleencorp.feen.service.stream.EventService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.fleencorp.base.util.ExceptionUtil.checkIsNullAny;
import static com.fleencorp.base.util.ExceptionUtil.checkIsTrue;
import static com.fleencorp.base.util.FleenUtil.toSearchResult;
import static com.fleencorp.feen.constant.stream.StreamAttendeeRequestToJoinStatus.APPROVED;
import static com.fleencorp.feen.constant.stream.StreamAttendeeRequestToJoinStatus.PENDING;
import static com.fleencorp.feen.constant.stream.StreamVisibility.PUBLIC;
import static com.fleencorp.feen.mapper.EventMapper.toEventResponse;
import static com.fleencorp.feen.mapper.FleenStreamMapper.toFleenStreamResponse;
import static com.fleencorp.feen.mapper.FleenStreamMapper.toFleenStreams;
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
  private final CountryService countryService;
  private final EventUpdateService eventUpdateService;
  private final FleenStreamRepository fleenStreamRepository;
  private final UserFleenStreamRepository userFleenStreamRepository;
  private final CalendarRepository calendarRepository;
  private final StreamAttendeeRepository streamAttendeeRepository;
  private final MemberRepository memberRepository;
  private final StreamEventPublisher streamEventPublisher;
  private static final int DEFAULT_NUMBER_OF_ATTENDEES_TO_GET_FOR_STREAM = 10;
  private final LocalizedResponse localizedResponse;

  /**
   * Constructor for the EventServiceImpl class.
   *
   * <p>Initializes an instance of EventServiceImpl with the necessary dependencies,
   * including Google Calendar event service, FleenStream repository, and Calendar repository.</p>
   *
   * @param delegatedAuthorityEmail the email address used for delegated authority
   * @param countryService the service providing country-related data and operations.
   * @param eventUpdateService the event service to handle update through services
   * @param fleenStreamRepository the repository for FleenStream operations
   * @param userFleenStreamRepository  the repository for FleenStream operations related to a user profile
   * @param calendarRepository the repository for calendar operations
   * @param streamAttendeeRepository the repository for managing event or stream attendees
   * @param memberRepository the repository for managing users
   * @param streamEventPublisher the service for publishing event or stream related actions
   * @param localizedResponse the service for creating localized response message
   */
  public EventServiceImpl(
      @Value("${google.delegated.authority.email}") final String delegatedAuthorityEmail,
      final CountryService countryService,
      final EventUpdateService eventUpdateService,
      final FleenStreamRepository fleenStreamRepository,
      final UserFleenStreamRepository userFleenStreamRepository,
      final CalendarRepository calendarRepository,
      final StreamAttendeeRepository streamAttendeeRepository,
      final MemberRepository memberRepository,
      final StreamEventPublisher streamEventPublisher,
      final LocalizedResponse localizedResponse) {
    super(fleenStreamRepository, streamAttendeeRepository, localizedResponse);
    this.delegatedAuthorityEmail = delegatedAuthorityEmail;
    this.countryService = countryService;
    this.eventUpdateService = eventUpdateService;
    this.fleenStreamRepository = fleenStreamRepository;
    this.userFleenStreamRepository = userFleenStreamRepository;
    this.calendarRepository = calendarRepository;
    this.streamAttendeeRepository = streamAttendeeRepository;
    this.memberRepository = memberRepository;
    this.streamEventPublisher = streamEventPublisher;
    this.localizedResponse = localizedResponse;
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
   * @return SearchResultView The result of the search, including event or stream details and pagination information.
   */
  @Override
  public SearchResultView findEvents(final CalendarEventSearchRequest searchRequest, final FleenUser user) {
    // Find events or streams based on the search request
    final PageAndFleenStreamResponse pageAndFleenStreamResponse = findEventsOrStreams(searchRequest);
    // Get the list of event or stream views from the search result
    final List<FleenStreamResponse> views = pageAndFleenStreamResponse.getResponses();
    // Determine the user's join status for each event or stream
    determineUserJoinStatusForEventOrStream(views, user);
    // Determine schedule status whether live, past or upcoming
    determineScheduleStatus(views);
    // Set other schedule details if user timezone is different
    setOtherScheduleBasedOnUserTimezone(views, user);
    // Set the attendees and total attendee count for each event or stream
    setStreamAttendeesAndTotalAttendeesAttending(views);
    // Get the first 10 attendees for each event or stream
    getFirst10AttendingInAnyOrder(views);
    // Convert the views and pagination details to a SearchResultView and return
    return toSearchResult(views, pageAndFleenStreamResponse.getPage());
  }

  /**
   * Finds events based on the search criteria provided in the CalendarEventSearchRequest and the specified StreamTimeType.
   *
   * <p>This method retrieves events from the repository based on whether they are upcoming, past, or live events,
   * as determined by the StreamTimeType. It then converts the events to FleenStreamResponse views and returns a SearchResultView.</p>
   *
   * @param searchRequest the search request containing search criteria
   * @param streamTimeType the type of events to retrieve (upcoming, past, or live)
   * @return a SearchResultView containing the events matching the search criteria
   */
  @Override
  public SearchResultView findEvents(final CalendarEventSearchRequest searchRequest, final StreamTimeType streamTimeType) {
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
    final List<FleenStreamResponse> views = toFleenStreams(page.getContent());
    // Set attendees and their total count for the retrieved events
    setStreamAttendeesAndTotalAttendeesAttending(views);
    // Retrieve the first 10 attendees for display purposes
    getFirst10AttendingInAnyOrder(views);
    // Return the constructed SearchResultView object
    return toSearchResult(views, page);
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
  private Page<FleenStream> getUpcomingEvents(final CalendarEventSearchRequest searchRequest) {
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
  private Page<FleenStream> getPastEvents(final CalendarEventSearchRequest searchRequest) {
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
  private Page<FleenStream> getLiveEvents(final CalendarEventSearchRequest searchRequest) {
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
   * @return a view of the search results containing the matching events
   */
  @Override
  public SearchResultView findMyEvents(final CalendarEventSearchRequest searchRequest, final FleenUser user) {
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
    final List<FleenStreamResponse> views = toFleenStreams(page.getContent());
    // Set other schedule details if user timezone is different
    setOtherScheduleBasedOnUserTimezone(views, user);
    // Set the attendees and total number of attendees for each event
    setStreamAttendeesAndTotalAttendeesAttending(views);
    // Retrieve the first 10 attendees in any order
    getFirst10AttendingInAnyOrder(views);
    // Return the search result view with event data and pagination details
    return toSearchResult(views, page);
  }

  /**
   * Finds events attended by a user based on the provided search request.
   *
   * <p>This method searches for events attended by the given user based on the search criteria specified in the request.
   * It retrieves events attended by the user within a date range, by title, or all attended events if no specific criteria are provided.</p>
   *
   * @param searchRequest the request containing search criteria such as date range and title
   * @param user the user whose attended events are to be found
   * @return a SearchResultView containing the events that match the search criteria
   */
  @Override
  public SearchResultView findEventsAttendedByUser(final CalendarEventSearchRequest searchRequest, final FleenUser user) {
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
    final List<FleenStreamResponse> views = toFleenStreams(page.getContent());
    // Set the attendees and total number of attendees for each event
    setStreamAttendeesAndTotalAttendeesAttending(views);
    // Retrieve the first 10 attendees in any order
    getFirst10AttendingInAnyOrder(views);
    // Return the search result view with event data and pagination details
    return toSearchResult(views, page);
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
   * @return a SearchResultView containing the list of events attended with another user
   */
  @Override
  public SearchResultView findEventsAttendedWithAnotherUser(final CalendarEventSearchRequest searchRequest, final FleenUser user) {
    final Page<FleenStream> page;

    if (nonNull(searchRequest.getAnotherUserId())) {
      // Retrieve events attended together by the current user and another user
      page = userFleenStreamRepository.findEventsAttendedTogether(user.toMember(), Member.of(searchRequest.getAnotherUserId()), searchRequest.getPage());
    } else {
      // Return an empty result if anotherUserId is not provided
      page = new PageImpl<>(List.of());
    }

    // Convert the event streams to response views
    final List<FleenStreamResponse> views = toFleenStreams(page.getContent());
    // Set the attendees and total number of attendees for each event
    setStreamAttendeesAndTotalAttendeesAttending(views);
    // Retrieve the first 10 attendees in any order
    getFirst10AttendingInAnyOrder(views);
    // Return the search result view with event data and pagination details
    return toSearchResult(views, page);
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
   * @return a {@link SearchResultView} containing the list of attendees and pagination details.
   */
  @Override
  public SearchResultView findEventAttendees(final Long eventId, final StreamAttendeeSearchRequest searchRequest) {
    return findEventOrStreamAttendees(eventId, searchRequest);
  }

  /**
   * Retrieves an event by its ID.
   *
   * <p>This method finds and returns the event identified by the eventId
   * from the FleenStream repository. It converts the retrieved FleenStream
   * object into a FleenStreamResponse for external presentation.</p>
   *
   * @param eventId the ID of the event to retrieve
   * @param user the authenticated user
   * @return {@link RetrieveEventResponse} containing the event details
   * @throws FleenStreamNotFoundException if the event with the specified ID is not found
   */
  @Override
  public RetrieveEventResponse retrieveEvent(final Long eventId, final FleenUser user) {
    final FleenStream stream = findStream(eventId);
    // Get all event or stream attendees
    final Set<StreamAttendee> streamAttendeesGoingToStream = getAttendeesGoingToStream(stream.getAttendees());
    // Convert the attendees to response objects
    final Set<StreamAttendeeResponse> streamAttendees = toStreamAttendeeResponses(streamAttendeesGoingToStream);
    // The Stream converted to a response
    final FleenStreamResponse streamResponse = toFleenStreamResponse(stream);

    if (nonNull(streamResponse)) {
      final List<FleenStreamResponse> streams = List.of(streamResponse);
      // Determine schedule status whether live, past or upcoming
      determineScheduleStatus(streams);
      // Set other schedule details if user timezone is different
      setOtherScheduleBasedOnUserTimezone(streams, user);
    }
    // Count total attendees whose request to join event is approved and are attending the event because they are interested
    final long totalAttendees = streamAttendeeRepository.countByFleenStreamAndStreamAttendeeRequestToJoinStatusAndIsAttending(stream, APPROVED, true);
    return localizedResponse.of(RetrieveEventResponse.of(eventId, streamResponse, streamAttendees, totalAttendees));
  }

  /**
   * Sets the number of attendees for each stream in the provided list of {@link FleenStreamResponse} objects.
   *
   * <p>This method iterates over a list of {@link FleenStreamResponse} instances, calculates the total number of attendees
   * who have been approved to join and are attending each event, and updates the total attending count for each stream.
   * The count is based on the data retrieved from the `streamAttendeeRepository.</p>
   *
   * @param streams the list of {@link FleenStreamResponse} objects whose attendee counts are to be updated.
   */
  protected void setStreamAttendeesAndTotalAttendeesAttending(final List<FleenStreamResponse> streams) {
    if (nonNull(streams)) {
      streams.stream()
        .filter(Objects::nonNull)
        .forEach(stream -> {
          final Long streamId = Long.parseLong(stream.getId().toString());
          // Count total attendees whose request to join event is approved and are attending the event because they are interested
          final long totalAttendees = streamAttendeeRepository.
              countByFleenStreamAndStreamAttendeeRequestToJoinStatusAndIsAttending(FleenStream.of(streamId), APPROVED, true);
          stream.setTotalAttending(totalAttendees);
      });
    }
  }

  /**
   * Retrieves and sets the first 10 attendees (or fewer if not enough attendees) for each stream in the provided list of {@link FleenStreamResponse} objects.
   *
   * <p>This method iterates over a list of {@link FleenStreamResponse} instances, retrieves up to 10 attendees who have been approved
   * to join and are attending each event, and updates the list of attendees for each stream. The retrieval is done in an arbitrary order.</p>
   *
   * @param streams the list of {@link FleenStreamResponse} objects whose attendee lists are to be updated.
   */
  protected void getFirst10AttendingInAnyOrder(final List<FleenStreamResponse> streams) {
    if (nonNull(streams)) {
      streams.stream()
        .filter(Objects::nonNull)
        .forEach(stream -> {
        final Long streamId = Long.parseLong(stream.getId().toString());
        // Create a pageable request to get the first 10 attendees
        final Pageable pageable = PageRequest.of(1, DEFAULT_NUMBER_OF_ATTENDEES_TO_GET_FOR_STREAM);
        // Fetch attendees who are approved and attending the event
        final Page<StreamAttendee> page = streamAttendeeRepository
            .findAllByFleenStreamAndStreamAttendeeRequestToJoinStatusAndIsAttending(FleenStream.of(streamId), APPROVED, true, pageable);
        // Convert the list of stream attendees to list of stream attendee responses
        final List<StreamAttendeeResponse> streamAttendees = toStreamAttendeeResponses(page.getContent());
        stream.setSomeAttendees(streamAttendees);
      });
    }
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
  protected Set<StreamAttendeeResponse> toStreamAttendeeResponsesWithStatus(final Set<StreamAttendee> streamAttendees) {
    // Convert each StreamAttendee entity to StreamAttendeeResponse and include request-to-join status
    if (nonNull(streamAttendees)) {
      return streamAttendees.stream()
          .filter(Objects::nonNull)
          .map(attendee -> {
            // Map each StreamAttendee to a StreamAttendeeResponse including request-to-join status
            final Long attendeeUserId = attendee.getMemberId();
            final String fullName = attendee.getFullName();

            // Return a new StreamAttendeeResponse with ID, user ID, full name, and request-to-join status
            return StreamAttendeeResponse.of(attendee.getStreamAttendeeId(), attendeeUserId, fullName, attendee.getStreamAttendeeRequestToJoinStatus());
          })
          .collect(Collectors.toSet());
    }
    return Set.of();
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
  protected List<StreamAttendeeResponse> toStreamAttendeeResponsesWithStatus(final List<StreamAttendee> streamAttendees) {
    final Set<StreamAttendee> streamAttendeesSet = new HashSet<>(streamAttendees);
    final Set<StreamAttendeeResponse> streamAttendeeResponses = toStreamAttendeeResponsesWithStatus(streamAttendeesSet);
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
    final Calendar calendar = findCalendar(user.getCountry());
    // Create a Calendar event request
    final CreateCalendarEventRequest createCalendarEventRequest = CreateCalendarEventRequest.by(createCalendarEventDto);

    // Set event organizer as attendee in Google calendar
    final String organizerAliasOrDisplayName = createCalendarEventDto.getOrganizerAlias(user.getFullName());
    final EventAttendeeOrGuest eventAttendeeOrGuest = EventAttendeeOrGuest.of(user.getEmailAddress(), organizerAliasOrDisplayName);

    // Add event organizer as an attendee
    createCalendarEventRequest.getAttendeeOrGuests().add(eventAttendeeOrGuest);
    // Update the event request with necessary details
    createCalendarEventRequest.update(createCalendarEventRequest, calendar.getExternalId(), delegatedAuthorityEmail, user.getEmailAddress());

    log.info("The request is {}", createCalendarEventRequest);
    // Create a FleenStream object from the DTO and update its details with the Google Calendar response
    FleenStream stream = createCalendarEventDto.toFleenStream(user.toMember());
    stream.updateDetails(
      organizerAliasOrDisplayName,
      user.getEmailAddress(),
      user.getPhoneNumber());

    // Save the event and and add the event in Google Calendar
    stream = fleenStreamRepository.save(stream);
    eventUpdateService.createEventInGoogleCalendar(stream, createCalendarEventRequest);

    return localizedResponse.of(CreateEventResponse.of(stream.getStreamId(), toEventResponse(stream)));
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

    // Save stream and create event in Google Calendar Event Service externally
    stream = fleenStreamRepository.save(stream);
    eventUpdateService.createInstantEventInGoogleCalendar(stream, createInstantCalendarEventRequest);

    return localizedResponse.of(CreateEventResponse.of(stream.getStreamId(), toEventResponse(stream)));
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
    final PatchCalendarEventRequest patchCalendarEventRequest = PatchCalendarEventRequest
        .of(calendar.getExternalId(),
            stream.getExternalId(),
            updateCalendarEventDto.getTitle(),
            updateCalendarEventDto.getDescription(),
            updateCalendarEventDto.getLocation());
    eventUpdateService.updateEventInGoogleCalendar(stream, patchCalendarEventRequest);

    return localizedResponse.of(UpdateEventResponse.of(stream.getStreamId(), toEventResponse(stream)));
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
    final Calendar calendar = findCalendar(user.getCountry());
    // Find the stream by its ID
    final FleenStream stream = findStream(eventId);

    // Validate if the user is the creator of the event
    validateCreatorOfEvent(stream, user);
    // Verify if the event or stream is still ongoing
    checkIsTrue(stream.isOngoing(), CannotCancelOrDeleteOngoingStreamException.of(eventId));
    // Update delete status of event
    stream.delete();
    fleenStreamRepository.save(stream);

    // Create a request to delete the calendar event
    final DeleteCalendarEventRequest deleteCalendarEventRequest = DeleteCalendarEventRequest
        .of(calendar.getExternalId(),
            stream.getExternalId());
    eventUpdateService.deleteEventInGoogleCalendar(deleteCalendarEventRequest);

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
    final Calendar calendar = findCalendar(user.getCountry());
    // Find the stream by its ID
    final FleenStream stream = findStream(eventId);

    // Verify stream details like the owner, event date and active status of the event
    verifyStreamDetails(stream, user);
    // Verify if the event or stream is still ongoing
    checkIsTrue(stream.isOngoing(), CannotCancelOrDeleteOngoingStreamException.of(eventId));
    // Update event status to canceled
    stream.cancel();
    fleenStreamRepository.save(stream);

    // Create a request to cancel the calendar event and submit request to external Calendar service
    final CancelCalendarEventRequest cancelCalendarEventRequest = CancelCalendarEventRequest
        .of(calendar.getExternalId(),
            stream.getExternalId());
    eventUpdateService.cancelEventInGoogleCalendar(cancelCalendarEventRequest);

    return localizedResponse.of(CancelEventResponse.of(eventId));
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
    final Calendar calendar = findCalendar(user.getCountry());
    // Find the stream by its ID
    final FleenStream stream = findStream(eventId);

    // Find the existing attendee record for the user and event
    streamAttendeeRepository.findByFleenStreamAndMember(stream, user.toMember())
      .ifPresent(streamAttendee -> {
        // If an attendee record exists, update their attendance status to false
        streamAttendee.setIsNotAttending();
        // Save the updated attendee record
        streamAttendeeRepository.save(streamAttendee);
        // Create a request that remove the attendee from the Google Calendar event
        final NotAttendingEventRequest notAttendingEventRequest = NotAttendingEventRequest.of(calendar.getExternalId(), stream.getExternalId(), user.getEmailAddress());
        eventUpdateService.notAttendingEvent(notAttendingEventRequest);
    });

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
    final Calendar calendar = findCalendar(user.getCountry());
    // Find the stream by its ID
    final FleenStream stream = findStream(eventId);

    // Verify stream details like the owner, event date and active status of the event
    verifyStreamDetails(stream, user);

    // Prepare a request to reschedule the calendar event with the new schedule details
    final RescheduleCalendarEventRequest rescheduleCalendarEventRequest = RescheduleCalendarEventRequest
        .of(calendar.getExternalId(),
            stream.getExternalId(),
            rescheduleCalendarEventDto.getActualStartDateTime(),
            rescheduleCalendarEventDto.getActualStartDateTime(),
            rescheduleCalendarEventDto.getTimezone());

    // Update Stream schedule details and time
    stream.updateSchedule(rescheduleCalendarEventDto.getActualStartDateTime(), rescheduleCalendarEventDto.getActualEndDateTime(), rescheduleCalendarEventDto.getTimezone());
    fleenStreamRepository.save(stream);
    // Update event schedule details in the Google Calendar service
    eventUpdateService.rescheduleEventInGoogleCalendar(rescheduleCalendarEventRequest);

    return localizedResponse.of(RescheduleEventResponse.of(eventId, toFleenStreamResponse(stream)));
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
    final Calendar calendar = findCalendar(user.getCountry());
    final FleenStream stream = joinEventOrStream(eventId, user);

    // Create a request to add the user as an attendee to the calendar event
    final AddNewEventAttendeeRequest addNewEventAttendeeRequest = AddNewEventAttendeeRequest
        .withComment(calendar.getExternalId(),
            stream.getExternalId(),
            user.getEmailAddress(),
            joinEventOrStreamDto.getComment());
    eventUpdateService.addNewAttendeeToCalendarEvent(addNewEventAttendeeRequest);

    return localizedResponse.of(JoinEventResponse.of(eventId));
  }

  /**
   * Requests to join an event.
   *
   * <p>This method allows a user to request to join a specified event. It first verifies if the event can still be joined,
   * checks if the user is already an attendee, and then creates a new StreamAttendee entry. If the event is public, the request
   * to join status is set to approved immediately. Otherwise, it remains pending until approved.</p>
   *
   * @param eventId               the ID of the event to join
   * @param requestToJoinEventOrStreamDto the dto containing details about user's request to join event
   * @param user                  the user requesting to join the event
   * @return {@link RequestToJoinEventResponse} response containing the event ID and message
   * @throws FleenStreamNotFoundException f the event is not found
   */
  @Override
  @Transactional
  public RequestToJoinEventResponse requestToJoinEvent(final Long eventId, final RequestToJoinEventOrStreamDto requestToJoinEventOrStreamDto, final FleenUser user) {
    // Find the stream by its ID
    final FleenStream stream = findStream(eventId);

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

    // Add the new StreamAttendee to the event's attendees list and save
    stream.getAttendees().add(streamAttendee);
    fleenStreamRepository.save(stream);

    return localizedResponse.of(RequestToJoinEventResponse.of(eventId));
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
        streamAttendee -> {
          // If the attendee's request is pending, proceed with processing
          if (streamAttendee.isPending()) {
            // Get the requested status for joining the event
            final StreamAttendeeRequestToJoinStatus requestToJoinStatus = processAttendeeRequestToJoinEventOrStreamDto.getActualJoinStatus();
            // Update the attendee's request status and set any organizer comments
            streamAttendee.updateRequestStatusAndSetOrganizerComment(requestToJoinStatus, processAttendeeRequestToJoinEventOrStreamDto.getComment());

            // If the request is approved, handle the external calendar invitation
            if (processAttendeeRequestToJoinEventOrStreamDto.isApproved()) {
              // Retrieve the calendar for the user's country
              final Calendar calendar = findCalendar(user.getCountry());
              // Save the updated attendee details
              streamAttendeeRepository.save(streamAttendee);
              // Add attendee to the event by invitation
              addAttendeeToEvent(calendar.getExternalId(), stream.getExternalId(), streamAttendee.getEmailAddress(), null);
            }
          }
        },
        () -> {}
      );

    // Return a localized response with the processed event details
    return localizedResponse.of(ProcessAttendeeRequestToJoinEventResponse.of(eventId, toFleenStreamResponse(stream)));
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
    stream.setStreamVisibility(updateEventOrStreamVisibilityDto.getActualVisibility());
    fleenStreamRepository.save(stream);

    // Create a request to update the event's visibility
    final UpdateCalendarEventVisibilityRequest updateCalendarEventVisibilityRequest = UpdateCalendarEventVisibilityRequest
      .of(calendar.getExternalId(),
          stream.getExternalId(),
          updateEventOrStreamVisibilityDto.getVisibility());
    // Update the event visibility on the Google Calendar Service
    eventUpdateService.updateEventVisibility(updateCalendarEventVisibilityRequest);
    // Send invitation to attendees that requested to join earlier and whose request is pending because the event or stream was private earlier
    sendInvitationToPendingAttendeesBasedOnCurrentStreamStatus(calendar.getExternalId(), stream, currentStreamVisibility);

    return localizedResponse.of(UpdateEventVisibilityResponse.of(eventId, toEventResponse(stream)));
  }

  /**
   * Sends invitations to pending attendees based on the current stream status and previous visibility.
   *
   * <p>This method checks if the provided FleenStream is null. If so, it throws an UnableToCompleteOperationException.</p>
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
   * @throws UnableToCompleteOperationException if the provided FleenStream object is null
   */
  public void sendInvitationToPendingAttendeesBasedOnCurrentStreamStatus(final String calendarExternalId, final FleenStream stream, final StreamVisibility previousStreamVisibility) {
    // Throw an exception if the any of the provided values is null
    checkIsNullAny(Set.of(stream, previousStreamVisibility), UnableToCompleteOperationException::new);
    // Determine the updated or current visibility of the stream
    final StreamVisibility currentStreamVisibility = stream.getStreamVisibility();

    // If the stream visibility is PUBLIC, and it was previously PRIVATE or PROTECTED
    if (StreamVisibility.isPublic(currentStreamVisibility) && StreamVisibility.isPrivateOrProtected(previousStreamVisibility)) {
      // Retrieve all pending attendees for the stream
      final List<StreamAttendee> streamAttendees = streamAttendeeRepository.findAllByFleenStreamAndStreamAttendeeRequestToJoinStatus(stream, PENDING);

      final Set<String> attendeesOrGuestsEmailAddresses = getAttendeesEmailAddresses(streamAttendees);
      final Set<Long> attendeeIds = getAttendeeIds(streamAttendees);

      // Approve users whose request was already pending when the event or stream was private or protected or locked
      streamAttendeeRepository.approveAllAttendeeRequestInvitation(APPROVED, new ArrayList<>(attendeeIds));

      // Create an event to add new attendees and publish it
      final AddCalendarEventAttendeesEvent addCalendarEventAttendeesEvent = AddCalendarEventAttendeesEvent
        .of(calendarExternalId,
            stream.getExternalId(),
            attendeesOrGuestsEmailAddresses,
            Set.of());
      streamEventPublisher.addNewAttendees(addCalendarEventAttendeesEvent);
    }
  }

  /**
   * Retrieves the set of attendee IDs from a list of StreamAttendee objects.
   *
   * @param attendees List of StreamAttendees from which the IDs will be extracted.
   * @return A set of attendee IDs.
   */
  protected Set<Long> getAttendeeIds(List<StreamAttendee> attendees) {
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
    final Calendar calendar = findCalendar(user.getCountry());
    // Find the stream by its ID
    final FleenStream stream = findStream(eventId);

    // Verify stream details like the owner, event date and active status of the event
    verifyStreamDetails(stream, user);

    streamAttendeeRepository.findDistinctByEmail(addNewEventAttendeeDto.getEmailAddress())
      .ifPresentOrElse(
        streamAttendee -> {
          if (isStreamAttendeeRequestPendingOrDisapproved(streamAttendee)) {
            // Approve new attendee request and set organizer comment
            approveAttendeeRequestAndSetOrganizerComment(streamAttendee, addNewEventAttendeeDto.getComment());
            // Add attendee to the event using an external service
            addAttendeeToEvent(calendar.getExternalId(), stream.getExternalId(), addNewEventAttendeeDto.getEmailAddress(), addNewEventAttendeeDto.getAliasOrDisplayName());
            streamAttendeeRepository.save(streamAttendee);
          }
        },
        () -> {
          // Check if there is a member with attendee's email address from the request and add them as a stream attendee
          approveAndAddAttendeeToStreamAttendees(stream, addNewEventAttendeeDto);
          // Add attendee to the event using an external service
          addAttendeeToEvent(calendar.getExternalId(), stream.getExternalId(), addNewEventAttendeeDto.getEmailAddress(), addNewEventAttendeeDto.getAliasOrDisplayName());
        }
      );

    fleenStreamRepository.save(stream);
    return localizedResponse.of(AddNewEventAttendeeResponse.of(eventId, toEventResponse(stream), addNewEventAttendeeDto.getEmailAddress()));
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
   * @return A {@link SearchResultView} object containing a list of {@link StreamAttendeeResponse} objects with a status of {@code PENDING}
   *         and pagination information.
   * @throws FleenStreamNotFoundException if no event with the given {@code eventId} is found.
   */
  @Override
  public SearchResultView getEventAttendeeRequestsToJoinEvent(final Long eventId, final StreamAttendeeSearchRequest searchRequest, final FleenUser user) {
    // Find the stream by its ID
    final FleenStream stream = findStream(eventId);
    // Validate owner of the event
    validateCreatorOfEvent(stream, user);

    final Page<StreamAttendee> page = streamAttendeeRepository.findByFleenStreamAndStreamAttendeeRequestToJoinStatus(stream, PENDING, searchRequest.getPage());
    final List<StreamAttendeeResponse> views = toStreamAttendeeResponsesWithStatus(page.getContent());
    return toSearchResult(views, page);
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
    return streamAttendee.isPending() || streamAttendee.isDisapproved();
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
      streamAttendee.setStreamAttendeeRequestToJoinStatus(APPROVED);
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
   * @return an EventAttendeesResponse DTO containing the list of attendees for the event
   * @throws FleenStreamNotFoundException if the event with the specified eventId is not found
   */
  @Override
  public EventOrStreamAttendeesResponse getEventAttendees(final Long eventId, final FleenUser user) {
     return getEventOrStreamAttendees(eventId, user);
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
    final AddNewEventAttendeeRequest addNewEventAttendeeRequest = AddNewEventAttendeeRequest
      .of(calendarExternalId,
          streamExternalId,
          attendeeEmailAddress,
          displayOrAliasName);

    eventUpdateService.addNewAttendeeToCalendarEvent(addNewEventAttendeeRequest);
  }

  /**
   * Finds a {@link Calendar} based on the provided country title.
   * This method retrieves the country code for the given title using the {@link CountryService}, and then
   * searches for a {@link Calendar} in the repository using the country code.
   * If the country title or the calendar is not found, an exception is thrown.
   *
   * @param countryTitle The title of the country for which the calendar is to be found.
   * @return The {@link Calendar} associated with the given country title.
   * @throws CalendarNotFoundException If no calendar is found for the provided country title or code.
   */
  protected Calendar findCalendar(final String countryTitle) {
    final String countryCode = countryService.getCountryCodeByTitle(countryTitle)
      .orElseThrow(() -> new CalendarNotFoundException(countryTitle));

    return calendarRepository.findDistinctByCodeIgnoreCase(countryCode)
      .orElseThrow(() -> new CalendarNotFoundException(countryCode));
  }

}
