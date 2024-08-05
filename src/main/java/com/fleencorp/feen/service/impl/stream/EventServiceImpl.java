package com.fleencorp.feen.service.impl.stream;

import com.fleencorp.base.model.view.search.SearchResultView;
import com.fleencorp.feen.constant.stream.StreamTimeType;
import com.fleencorp.feen.constant.stream.StreamVisibility;
import com.fleencorp.feen.event.publisher.StreamEventPublisher;
import com.fleencorp.feen.exception.calendar.CalendarNotFoundException;
import com.fleencorp.feen.exception.stream.*;
import com.fleencorp.feen.mapper.StreamAttendeeMapper;
import com.fleencorp.feen.model.domain.calendar.Calendar;
import com.fleencorp.feen.model.domain.stream.FleenStream;
import com.fleencorp.feen.model.domain.stream.StreamAttendee;
import com.fleencorp.feen.model.domain.user.Member;
import com.fleencorp.feen.model.dto.event.*;
import com.fleencorp.feen.model.dto.event.CreateCalendarEventDto.EventAttendeeOrGuest;
import com.fleencorp.feen.model.event.AddCalendarEventAttendeesEvent;
import com.fleencorp.feen.model.request.calendar.event.*;
import com.fleencorp.feen.model.request.search.calendar.CalendarEventSearchRequest;
import com.fleencorp.feen.model.request.search.stream.StreamAttendeeSearchRequest;
import com.fleencorp.feen.model.response.base.FleenFeenResponse;
import com.fleencorp.feen.model.response.base.FleenStreamResponse;
import com.fleencorp.feen.model.response.base.StreamAttendeeResponse;
import com.fleencorp.feen.model.response.event.*;
import com.fleencorp.feen.model.security.FleenUser;
import com.fleencorp.feen.repository.calendar.CalendarRepository;
import com.fleencorp.feen.repository.stream.FleenStreamRepository;
import com.fleencorp.feen.repository.stream.StreamAttendeeRepository;
import com.fleencorp.feen.repository.stream.UserFleenStreamRepository;
import com.fleencorp.feen.repository.user.MemberRepository;
import com.fleencorp.feen.service.stream.EventService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.fleencorp.base.util.FleenUtil.areNotEmpty;
import static com.fleencorp.base.util.FleenUtil.toSearchResult;
import static com.fleencorp.feen.constant.stream.StreamAttendeeRequestToJoinStatus.*;
import static com.fleencorp.feen.constant.stream.StreamStatus.CANCELLED;
import static com.fleencorp.feen.constant.stream.StreamVisibility.*;
import static com.fleencorp.feen.mapper.EventMapper.toEventResponse;
import static com.fleencorp.feen.mapper.FleenStreamMapper.toFleenStreamResponse;
import static com.fleencorp.feen.mapper.FleenStreamMapper.toFleenStreams;
import static com.fleencorp.feen.util.ExceptionUtil.checkIsNull;
import static com.fleencorp.feen.util.ExceptionUtil.checkIsNullAny;
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
public class EventServiceImpl implements EventService {

  private final String delegatedAuthorityEmail;
  private final EventUpdateService eventUpdateService;
  private final FleenStreamRepository fleenStreamRepository;
  private final UserFleenStreamRepository userFleenStreamRepository;
  private final CalendarRepository calendarRepository;
  private final StreamAttendeeRepository streamAttendeeRepository;
  private final MemberRepository memberRepository;
  private final StreamEventPublisher streamEventPublisher;
  private static final int DEFAULT_NUMBER_OF_ATTENDEES_TO_GET_FOR_STREAM = 10;

  /**
   * Constructor for the EventServiceImpl class.
   *
   * <p>Initializes an instance of EventServiceImpl with the necessary dependencies,
   * including Google Calendar event service, FleenStream repository, and Calendar repository.</p>
   *
   * @param delegatedAuthorityEmail the email address used for delegated authority
   * @param eventUpdateService the event service to handle update through services
   * @param fleenStreamRepository the repository for FleenStream operations
   * @param userFleenStreamRepository  the repository for FleenStream operations related to a user profile
   * @param calendarRepository the repository for calendar operations
   * @param streamAttendeeRepository the repository for managing event or stream attendees
   * @param memberRepository the repository for managing users
   * @param streamEventPublisher the service for publishing stream or event related actions
   */
  public EventServiceImpl(
      @Value("${google.delegated.authority.email}") final String delegatedAuthorityEmail,
      final EventUpdateService eventUpdateService,
      final FleenStreamRepository fleenStreamRepository,
      final UserFleenStreamRepository userFleenStreamRepository,
      final CalendarRepository calendarRepository,
      final StreamAttendeeRepository streamAttendeeRepository,
      final MemberRepository memberRepository,
      final StreamEventPublisher streamEventPublisher) {
    this.delegatedAuthorityEmail = delegatedAuthorityEmail;
    this.eventUpdateService = eventUpdateService;
    this.fleenStreamRepository = fleenStreamRepository;
    this.userFleenStreamRepository = userFleenStreamRepository;
    this.calendarRepository = calendarRepository;
    this.streamAttendeeRepository = streamAttendeeRepository;
    this.memberRepository = memberRepository;
    this.streamEventPublisher = streamEventPublisher;
  }

  /**
   * Finds events based on the search criteria provided in the CalendarEventSearchRequest.
   *
   * <p>This method retrieves events from the FleenStream repository based on the specified
   * search criteria such as start date, end date, or event title. It then maps the retrieved
   * events to FleenStreamResponse objects and returns a SearchResultView containing the
   * search results.</p>
   *
   * @param searchRequest the request containing the search criteria
   * @return a SearchResultView containing the search results
   */
  @Override
  public SearchResultView findEvents(final CalendarEventSearchRequest searchRequest) {
    final Page<FleenStream> page;
    if (areNotEmpty(searchRequest.getStartDate(), searchRequest.getEndDate())) {
      page = fleenStreamRepository.findByDateBetween(searchRequest.getStartDate().atStartOfDay(), searchRequest.getEndDate().atStartOfDay(), searchRequest.getPage());
    } else if (nonNull(searchRequest.getTitle())) {
      page = fleenStreamRepository.findByTitle(searchRequest.getTitle(), searchRequest.getPage());
    } else {
      page = fleenStreamRepository.findMany(searchRequest.getPage());
    }

    final List<FleenStreamResponse> views = toFleenStreams(page.getContent());
    setStreamAttendeesAndTotalAttendeesAttending(views);
    getFirst10AttendingInAnyOrder(views);
    return toSearchResult(views, page);
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
    if (streamTimeType == StreamTimeType.UPCOMING) {
      page = getUpcomingEvents(searchRequest);
    } else if (streamTimeType == StreamTimeType.PAST) {
      page = getPastEvents(searchRequest);
    } else {
      page = getLiveEvents(searchRequest);
    }

    final List<FleenStreamResponse> views = toFleenStreams(page.getContent());
    setStreamAttendeesAndTotalAttendeesAttending(views);
    getFirst10AttendingInAnyOrder(views);
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
  public SearchResultView findEvents(final CalendarEventSearchRequest searchRequest, final FleenUser user) {
    final Page<FleenStream> page;
    final StreamVisibility streamVisibility = searchRequest.getVisibility(PUBLIC);

    if (areNotEmpty(searchRequest.getStartDate(), searchRequest.getEndDate()) && nonNull(searchRequest.getStreamVisibility())) {
      page = userFleenStreamRepository.findByDateBetweenAndUser(searchRequest.getStartDate().atStartOfDay(), searchRequest.getEndDate().atStartOfDay(),
          streamVisibility, user.toMember(), searchRequest.getPage());
    } else if (areNotEmpty(searchRequest.getStartDate(), searchRequest.getEndDate())) {
      page = userFleenStreamRepository.findByDateBetweenAndUser(searchRequest.getStartDate().atStartOfDay(), searchRequest.getEndDate().atStartOfDay(),
          user.toMember(), searchRequest.getPage());
    } else if (nonNull(searchRequest.getTitle()) && nonNull(searchRequest.getStreamVisibility())) {
      page = userFleenStreamRepository.findByTitleAndUser(searchRequest.getTitle(), streamVisibility, user.toMember(), searchRequest.getPage());
    } else if (nonNull(searchRequest.getTitle())) {
      page = userFleenStreamRepository.findByTitleAndUser(searchRequest.getTitle(), user.toMember(), searchRequest.getPage());
    } else {
      page = userFleenStreamRepository.findManyByMe(user.toMember(), searchRequest.getPage());
    }

    final List<FleenStreamResponse> views = toFleenStreams(page.getContent());
    setStreamAttendeesAndTotalAttendeesAttending(views);
    getFirst10AttendingInAnyOrder(views);
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
    if (areNotEmpty(searchRequest.getStartDate(), searchRequest.getEndDate())) {
      page = userFleenStreamRepository.findAttendedByDateBetweenAndUser(searchRequest.getStartDate().atStartOfDay(), searchRequest.getEndDate().atStartOfDay(),
          user.toMember(), searchRequest.getPage());
    } else if (nonNull(searchRequest.getTitle())) {
      page = userFleenStreamRepository.findAttendedByTitleAndUser(searchRequest.getTitle(), user.toMember(), searchRequest.getPage());
    } else {
      page = userFleenStreamRepository.findAttendedByUser(user.toMember(), searchRequest.getPage());
    }

    final List<FleenStreamResponse> views = toFleenStreams(page.getContent());
    setStreamAttendeesAndTotalAttendeesAttending(views);
    getFirst10AttendingInAnyOrder(views);
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
    // Retrieve events attended together by the current user and another user
    if (nonNull(searchRequest.getAnotherUserId())) {
      page = userFleenStreamRepository.findEventsAttendedTogether(user.toMember(), Member.of(searchRequest.getAnotherUserId()), searchRequest.getPage());
    } else {
      page = new PageImpl<>(List.of());
    }

    final List<FleenStreamResponse> views = toFleenStreams(page.getContent());
    setStreamAttendeesAndTotalAttendeesAttending(views);
    getFirst10AttendingInAnyOrder(views);
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
    final Page<StreamAttendee> page = streamAttendeeRepository.findByFleenStream(FleenStream.of(eventId), searchRequest.getPage());
    final List<StreamAttendeeResponse> views = toStreamAttendeeResponses(page.getContent());
    return toSearchResult(views, page);
  }

  /**
   * Retrieves an event by its ID.
   *
   * <p>This method finds and returns the event identified by the eventId
   * from the FleenStream repository. It converts the retrieved FleenStream
   * object into a FleenStreamResponse for external presentation.</p>
   *
   * @param eventId the ID of the event to retrieve
   * @return {@link RetrieveEventResponse} containing the event details
   * @throws FleenStreamNotFoundException if the event with the specified ID is not found
   */
  @Override
  public RetrieveEventResponse retrieveEvent(final Long eventId) {
    final FleenStream stream = fleenStreamRepository.findById(eventId)
        .orElseThrow(() -> new FleenStreamNotFoundException(eventId));
    // Get all event or stream attendees
    final Set<StreamAttendeeResponse> streamAttendees = toStreamAttendeeResponses(stream.getAttendees());
    // Count total attendees whose request to join event is approved and are attending the event because they are interested
    final long totalAttendees = streamAttendeeRepository.countByFleenStreamAndStreamAttendeeRequestToJoinStatusAndIsAttending(stream, APPROVED, true);
    return RetrieveEventResponse.of(eventId, toFleenStreamResponse(stream), streamAttendees, totalAttendees);
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
      streams.forEach(stream -> {
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
      streams.forEach(stream -> {
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
   * Converts a set of {@link StreamAttendee} entities to a set of {@link StreamAttendeeResponse} objects.
   *
   * <p>This method transforms each {@code StreamAttendee} entity into a corresponding {@code StreamAttendeeResponse},
   * which includes the attendee's ID and full name. If the provided set of {@code StreamAttendee} is {@code null},
   * an empty set is returned.</p>
   *
   * @param streamAttendees the set of {@code StreamAttendee} entities to convert.
   * @return a set of {@code StreamAttendeeResponse} objects or an empty set if the input is {@code null}.
   */
  protected Set<StreamAttendeeResponse> toStreamAttendeeResponses(final Set<StreamAttendee> streamAttendees) {
    if (nonNull(streamAttendees)) {
      return streamAttendees
          .stream()
          .map(streamAttendee -> StreamAttendeeResponse.of(streamAttendee.getStreamAttendeeId(), streamAttendee.getMember().getFullName()))
          .collect(Collectors.toSet());
    }
    return Set.of();
  }

  /**
   * Converts a list of {@link StreamAttendee} entities into a list of {@link StreamAttendeeResponse} objects.
   *
   * <p>This method first converts the list of {@code StreamAttendee} entities into a set to eliminate any duplicate
   * attendees. It then uses the {@code toStreamAttendeeResponses} method to perform the actual conversion,
   * and finally returns the result as a list.</p>
   *
   * @param streamAttendees the list of {@code StreamAttendee} entities to convert.
   * @return a list of {@code StreamAttendeeResponse} objects, with duplicates removed.
   */
  protected List<StreamAttendeeResponse> toStreamAttendeeResponses(final List<StreamAttendee> streamAttendees) {
    final Set<StreamAttendee> streamAttendeesSet = new HashSet<>(streamAttendees);
    final Set<StreamAttendeeResponse> streamAttendeeResponses = toStreamAttendeeResponses(streamAttendeesSet);
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
  public CreateEventResponse createEvent(final CreateCalendarEventDto createCalendarEventDto, final FleenUser user) {
    final Calendar calendar = calendarRepository.findDistinctByCodeIgnoreCase(user.getCountry())
        .orElseThrow(() -> new CalendarNotFoundException(user.getCountry()));

    final CreateCalendarEventRequest createCalendarEventRequest = CreateCalendarEventRequest.by(createCalendarEventDto);

    // Set event organizer as attendee in Google calendar
    final EventAttendeeOrGuest eventAttendeeOrGuest = new EventAttendeeOrGuest();
    eventAttendeeOrGuest.setEmailAddress(user.getEmailAddress());
    eventAttendeeOrGuest.setAliasOrDisplayName(user.getFullName());
    eventAttendeeOrGuest.setIsOrganizer(true);

    // Add event organizer as an attendee
    createCalendarEventRequest.getAttendeeOrGuestEmailAddresses().add(eventAttendeeOrGuest);
    // Update the event request with necessary details
    createCalendarEventRequest.update(createCalendarEventRequest, calendar.getExternalId(), delegatedAuthorityEmail, user.getEmailAddress());

    // Create a FleenStream object from the DTO and update its details with the Google Calendar response
    FleenStream stream = createCalendarEventDto.toFleenStream(user.toMember());
    stream.updateDetails(
      user.getFullName(),
      user.getEmailAddress(),
      user.getPhoneNumber());

    stream = fleenStreamRepository.save(stream);
    eventUpdateService.createEventInGoogleCalendar(stream, createCalendarEventRequest);

    return CreateEventResponse.of(stream.getFleenStreamId(), toEventResponse(stream));
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
  public CreateEventResponse createInstantEvent(final CreateInstantCalendarEventDto createInstantCalendarEventDto, final FleenUser user) {
    final Calendar calendar = calendarRepository.findDistinctByCodeIgnoreCase(user.getCountry())
        .orElseThrow(() -> new CalendarNotFoundException(user.getCountry()));

    final CreateInstantCalendarEventRequest createInstantCalendarEventRequest = CreateInstantCalendarEventRequest.by(createInstantCalendarEventDto);
    // Update the instant event request with necessary details
    createInstantCalendarEventRequest.update(calendar.getExternalId());

    // Create a FleenStream object from the DTO and update its details with the Google Calendar response
    FleenStream stream = createInstantCalendarEventDto.toFleenStream();
    stream.updateDetails(
      user.getFullName(),
      user.getEmailAddress(),
      user.getPhoneNumber());

    stream = fleenStreamRepository.save(stream);
    eventUpdateService.createInstantEventInGoogleCalendar(stream, createInstantCalendarEventRequest);

    return CreateEventResponse.of(stream.getFleenStreamId(), toEventResponse(stream));
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
  public UpdateEventResponse updateEvent(final Long eventId, final UpdateCalendarEventDto updateCalendarEventDto, final FleenUser user) {
    final Calendar calendar = calendarRepository.findDistinctByCodeIgnoreCase(user.getCountry())
        .orElseThrow(() -> new CalendarNotFoundException(user.getCountry()));

    FleenStream stream = fleenStreamRepository.findById(eventId)
        .orElseThrow(() -> new FleenStreamNotFoundException(eventId));

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
            updateCalendarEventDto.getDescription());
    eventUpdateService.updateEventInGoogleCalendar(stream, patchCalendarEventRequest);

    return UpdateEventResponse.of(stream.getFleenStreamId(), toEventResponse(stream));
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
  public DeleteEventResponse deleteEvent(final Long eventId, final FleenUser user) {
    final Calendar calendar = calendarRepository.findDistinctByCodeIgnoreCase(user.getCountry())
        .orElseThrow(() -> new CalendarNotFoundException(user.getCountry()));

    final FleenStream stream = fleenStreamRepository.findById(eventId)
        .orElseThrow(() -> new FleenStreamNotFoundException(eventId));

    // Validate if the user is the creator of the event
    validateCreatorOfEvent(stream, user);

    stream.delete();
    fleenStreamRepository.save(stream);

    // Create a request to delete the calendar event
    final DeleteCalendarEventRequest deleteCalendarEventRequest = DeleteCalendarEventRequest
        .of(calendar.getExternalId(),
            stream.getExternalId());
    eventUpdateService.deleteEventInGoogleCalendar(deleteCalendarEventRequest);

    return DeleteEventResponse.of(eventId);
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
  public CancelEventResponse cancelEvent(final Long eventId, final FleenUser user) {
    final Calendar calendar = calendarRepository.findDistinctByCodeIgnoreCase(user.getCountry())
        .orElseThrow(() -> new CalendarNotFoundException(user.getCountry()));

    final FleenStream stream = fleenStreamRepository.findById(eventId)
        .orElseThrow(() -> new FleenStreamNotFoundException(eventId));

    // Verify stream details like the owner, event date and active status of the event
    verifyStreamDetails(stream, user);

    stream.cancel();
    fleenStreamRepository.save(stream);

    // Create a request to cancel the calendar event
    final CancelCalendarEventRequest cancelCalendarEventRequest = CancelCalendarEventRequest
        .of(calendar.getExternalId(),
            stream.getExternalId());
    eventUpdateService.cancelEventInGoogleCalendar(cancelCalendarEventRequest);

    return CancelEventResponse.of(eventId);
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
  public NotAttendingEventResponse notAttendingEvent(final Long eventId, final FleenUser user) {
    final Calendar calendar = calendarRepository.findDistinctByCodeIgnoreCase(user.getCountry())
      .orElseThrow(() -> new CalendarNotFoundException(user.getCountry()));

    final FleenStream stream = fleenStreamRepository.findById(eventId)
      .orElseThrow(() -> new FleenStreamNotFoundException(eventId));

    // Find the existing attendee record for the user and event
    final Optional<StreamAttendee> existingStreamAttendee = streamAttendeeRepository.findByFleenStreamAndMember(stream, user.toMember());
    if (existingStreamAttendee.isPresent()) {
      // If an attendee record exists, update their attendance status to false
      final StreamAttendee attendee = existingStreamAttendee.get();
      attendee.setIsAttending(false);
      // Save the updated attendee record
      streamAttendeeRepository.save(attendee);
      // Create a request that remove the attendee from the Google Calendar event
      NotAttendingEventRequest notAttendingEventRequest = NotAttendingEventRequest.of(calendar.getExternalId(), stream.getExternalId(), user.getEmailAddress());
      eventUpdateService.notAttendingEvent(notAttendingEventRequest);
    }
    return NotAttendingEventResponse.of();
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
  public RescheduleEventResponse rescheduleEvent(final Long eventId, final RescheduleCalendarEventDto rescheduleCalendarEventDto, final FleenUser user) {
    final Calendar calendar = calendarRepository.findDistinctByCodeIgnoreCase(user.getCountry())
        .orElseThrow(() -> new CalendarNotFoundException(user.getCountry()));

    final FleenStream stream = fleenStreamRepository.findById(eventId)
        .orElseThrow(() -> new FleenStreamNotFoundException(eventId));

    // Verify stream details like the owner, event date and active status of the event
    verifyStreamDetails(stream, user);

    // Prepare a request to reschedule the calendar event with the new schedule details
    final RescheduleCalendarEventRequest rescheduleCalendarEventRequest = RescheduleCalendarEventRequest
        .of(calendar.getExternalId(),
            stream.getExternalId(),
            rescheduleCalendarEventDto.getStartDateTime(),
            rescheduleCalendarEventDto.getEndDateTime(),
            rescheduleCalendarEventDto.getTimezone());

    stream.updateSchedule(rescheduleCalendarEventDto.getStartDateTime(), rescheduleCalendarEventDto.getEndDateTime(), rescheduleCalendarEventDto.getTimezone());
    fleenStreamRepository.save(stream);
    eventUpdateService.rescheduleEventInGoogleCalendar(rescheduleCalendarEventRequest);

    return RescheduleEventResponse.of(eventId, toFleenStreamResponse(stream));
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
  public FleenFeenResponse joinEvent(final Long eventId, final FleenUser user) {
    final Calendar calendar = calendarRepository.findDistinctByCodeIgnoreCase(user.getCountry())
        .orElseThrow(() -> new CalendarNotFoundException(user.getCountry()));

    final FleenStream stream = fleenStreamRepository.findById(eventId)
        .orElseThrow(() -> new FleenStreamNotFoundException(eventId));

    // Verify event is not canceled
    verifyEventIsNotCancelled(stream);
    // Check if the stream is still active and can be joined.
    verifyStreamEndDate(stream.getScheduledEndDate());
    // Check if the stream is private
    checkIfStreamIsPrivate(eventId, stream);
    // Check if the user is already an attendee
    checkIfUserIsAlreadyAnAttendeeAndThrowError(stream, user.getId());
    // Create a new StreamAttendee entry for the user
    final StreamAttendee streamAttendee = createStreamAttendee(stream, user);
    streamAttendee.setStreamAttendeeRequestToJoinStatus(APPROVED);

    // Add the new StreamAttendee to the event's attendees list and save
    stream.getAttendees().add(streamAttendee);
    fleenStreamRepository.save(stream);
    streamAttendeeRepository.save(streamAttendee);

    // Create a request to add the user as an attendee to the calendar event
    final AddNewEventAttendeeRequest addNewEventAttendeeRequest = AddNewEventAttendeeRequest
        .of(calendar.getExternalId(),
            stream.getExternalId(),
            user.getEmailAddress());
    eventUpdateService.addNewAttendeeToCalendarEvent(addNewEventAttendeeRequest);

    return FleenFeenResponse.of(eventId);
  }

  /**
   * Requests to join an event.
   *
   * <p>This method allows a user to request to join a specified event. It first verifies if the event can still be joined,
   * checks if the user is already an attendee, and then creates a new StreamAttendee entry. If the event is public, the request
   * to join status is set to approved immediately. Otherwise, it remains pending until approved.</p>
   *
   * @param eventId               the ID of the event to join
   * @param requestToJoinEventDto the dto containing details about user's request to join event
   * @param user                  the user requesting to join the event
   * @return {@link RequestToJoinEventResponse} response containing the event ID and message
   * @throws FleenStreamNotFoundException f the event is not found
   */
  @Override
  public RequestToJoinEventResponse requestToJoinEvent(final Long eventId, final RequestToJoinEventDto requestToJoinEventDto, final FleenUser user) {
    final FleenStream stream = fleenStreamRepository.findById(eventId)
        .orElseThrow(() -> new FleenStreamNotFoundException(eventId));

    // Check if the stream is still active and can be joined.
    verifyStreamEndDate(stream.getScheduledEndDate());
    // Check if event is not cancelled
    verifyEventIsNotCancelled(stream);
    // CHeck if the user is already an attendee
    checkIfUserIsAlreadyAnAttendeeAndThrowError(stream, user.getId());
    // Create a new StreamAttendee entry for the user
    final StreamAttendee streamAttendee = createStreamAttendeeWithComment(stream, user, requestToJoinEventDto.getComment());

    // If the event is private, set the request to join status to pending
    if (stream.getStreamVisibility() == PRIVATE || stream.getStreamVisibility() == PROTECTED) {
      streamAttendee.setStreamAttendeeRequestToJoinStatus(PENDING);
    }

    // Add the new StreamAttendee to the event's attendees list and save
    stream.getAttendees().add(streamAttendee);
    fleenStreamRepository.save(stream);
    streamAttendeeRepository.save(streamAttendee);

    return RequestToJoinEventResponse.of(eventId);
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
   * @param processAttendeeRequestToJoinEventDto the DTO containing the details of the attendee's request
   * @param user                                 the user who is processing the request
   * @return {@link ProcessAttendeeRequestToJoinEventResponse} a response containing the result of processing the request
   * @throws FleenStreamNotFoundException if the stream or event cannot be found or does not exist
   * @throws CalendarNotFoundException if the calendar associated with event cannot be found or does not exist
   */
  @Override
  public ProcessAttendeeRequestToJoinEventResponse processAttendeeRequestToJoinEvent(final Long eventId, final ProcessAttendeeRequestToJoinEventDto processAttendeeRequestToJoinEventDto, final FleenUser user) {
    final FleenStream stream = fleenStreamRepository.findById(eventId)
        .orElseThrow(() -> new FleenStreamNotFoundException(eventId));

    // Verify stream details like the owner, event date and active status of the event
    verifyStreamDetails(stream, user);

    final Optional<StreamAttendee> existingStreamAttendee = checkIfUserIsAlreadyAnAttendee(stream, Long.parseLong(processAttendeeRequestToJoinEventDto.getAttendeeUserId()));
    // Check if the attendee has submitted a request and the request is still pending
    if (existingStreamAttendee.isPresent() && existingStreamAttendee.get().getStreamAttendeeRequestToJoinStatus() == PENDING) {
      final StreamAttendee streamAttendee = existingStreamAttendee.get();
      streamAttendee.setStreamAttendeeRequestToJoinStatus(processAttendeeRequestToJoinEventDto.getActualJoinStatus());
      streamAttendee.setOrganizerComment(processAttendeeRequestToJoinEventDto.getComment());

      if (processAttendeeRequestToJoinEventDto.getActualJoinStatus() == APPROVED) {
        final Calendar calendar = calendarRepository.findDistinctByCodeIgnoreCase(user.getCountry())
            .orElseThrow(() -> new CalendarNotFoundException(user.getCountry()));

        streamAttendeeRepository.save(streamAttendee);
        fleenStreamRepository.save(stream);

        // Add attendee to the event by invitation
        addAttendeeToEvent(calendar.getExternalId(), stream.getExternalId(), streamAttendee.getMember().getEmailAddress(), null);
      }
    }

    return ProcessAttendeeRequestToJoinEventResponse.of(eventId, toFleenStreamResponse(stream));
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
   * @param updateEventVisibilityDto the DTO containing the new visibility status
   * @param user                     the user requesting the update
   * @return {@link UpdateEventVisibilityResponse} containing the event and event details
   * @throws CalendarNotFoundException if the calendar associated with event cannot be found or does not exist
   * @throws FleenStreamNotFoundException if the stream or event cannot be found or does not exist
   */
  @Override
  public UpdateEventVisibilityResponse updateEventVisibility(final Long eventId, final UpdateEventVisibilityDto updateEventVisibilityDto, final FleenUser user) {
    final Calendar calendar = calendarRepository.findDistinctByCodeIgnoreCase(user.getCountry())
        .orElseThrow(() -> new CalendarNotFoundException(user.getCountry()));

    final FleenStream stream = fleenStreamRepository.findById(eventId)
        .orElseThrow(() -> new FleenStreamNotFoundException(eventId));

    // Retrieve the current or existing status or visibility status of a stream
    final StreamVisibility currentStreamVisibility = stream.getStreamVisibility();
    // Verify stream details like the owner, event date and active status of the event
    verifyStreamDetails(stream, user);
    // Update the visibility of an event or stream
    stream.setStreamVisibility(updateEventVisibilityDto.getActualVisibility());
    fleenStreamRepository.save(stream);

    // Create a request to update the event's visibility
    final UpdateCalendarEventVisibilityRequest updateCalendarEventVisibilityRequest = UpdateCalendarEventVisibilityRequest
      .of(calendar.getExternalId(),
          stream.getExternalId(),
          updateEventVisibilityDto.getVisibility());
    // Update the event visibility on the Google Calendar Service
    eventUpdateService.updateEventVisibility(updateCalendarEventVisibilityRequest);
    // Send invitation to attendees that requested to join earlier and whose request is pending because the event or stream was private earlier
    sendInvitationToPendingAttendeesBasedOnCurrentStreamStatus(calendar.getExternalId(), stream, currentStreamVisibility);

    return UpdateEventVisibilityResponse.of(eventId, toEventResponse(stream));
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
    if (currentStreamVisibility == PUBLIC && (previousStreamVisibility == PRIVATE || previousStreamVisibility == PROTECTED)) {
      // Retrieve all pending attendees for the stream
      final List<StreamAttendee> streamAttendees = streamAttendeeRepository.findAllByFleenStreamAndStreamAttendeeRequestToJoinStatus(stream, PENDING);

      final Set<String> attendeesOrGuestsEmailAddresses = getAttendeesEmailAddresses(streamAttendees);
      final Set<Long> attendeeIds = streamAttendees.stream()
        .map(StreamAttendee::getStreamAttendeeId)
        .collect(Collectors.toSet());

      streamAttendeeRepository.approveAllAttendeeRequestInvitation(APPROVED, attendeeIds);

      // Create an event to add new attendees and publish it
      final AddCalendarEventAttendeesEvent addCalendarEventAttendeesEvent = AddCalendarEventAttendeesEvent
        .of(calendarExternalId,
            stream.getExternalId(),
            attendeesOrGuestsEmailAddresses);
      streamEventPublisher.addNewAttendees(addCalendarEventAttendeesEvent);
    }
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
          .map(attendee -> attendee.getMember().getEmailAddress())
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
  public AddNewEventAttendeeResponse addEventAttendee(final Long eventId, final AddNewEventAttendeeDto addNewEventAttendeeDto, final FleenUser user) {
    final Calendar calendar = calendarRepository.findDistinctByCodeIgnoreCase(user.getCountry())
        .orElseThrow(() -> new CalendarNotFoundException(user.getCountry()));

    final FleenStream stream = fleenStreamRepository.findById(eventId)
        .orElseThrow(() -> new FleenStreamNotFoundException(eventId));

    // Verify stream details like the owner, event date and active status of the event
    verifyStreamDetails(stream, user);

    final Optional<StreamAttendee> existingStreamAttendee = streamAttendeeRepository.findDistinctByEmail(addNewEventAttendeeDto.getEmailAddress());
    if (existingStreamAttendee.isPresent() && isStreamAttendeeRequestPendingOrDisapproved(existingStreamAttendee.get())) {
      final StreamAttendee streamAttendee = existingStreamAttendee.get();
      // Approve new attendee request and set organizer comment
      approveAttendeeRequestAndSetOrganizerComment(streamAttendee, addNewEventAttendeeDto.getComment());
      // Add attendee to the event using an external service
      addAttendeeToEvent(calendar.getExternalId(), stream.getExternalId(), addNewEventAttendeeDto.getEmailAddress(), addNewEventAttendeeDto.getAliasOrDisplayName());
      streamAttendeeRepository.save(streamAttendee);
    } else {
      // Check if there is a member with attendee's email address from the request and add them as a stream attendee
      approveAndAddAttendeeToStreamAttendees(stream, addNewEventAttendeeDto);
      // Add attendee to the event using an external service
      addAttendeeToEvent(calendar.getExternalId(), stream.getExternalId(), addNewEventAttendeeDto.getEmailAddress(), addNewEventAttendeeDto.getAliasOrDisplayName());
    }

    fleenStreamRepository.save(stream);
    return AddNewEventAttendeeResponse.of(eventId, toEventResponse(stream), addNewEventAttendeeDto.getEmailAddress());
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
    return streamAttendee.getStreamAttendeeRequestToJoinStatus() == PENDING || streamAttendee.getStreamAttendeeRequestToJoinStatus() == DISAPPROVED;
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
    final Optional<Member> existingMember = memberRepository.findByEmailAddress(addNewEventAttendeeDto.getEmailAddress());
    // If the member exists, proceed to create and approve the attendee
    if (existingMember.isPresent()) {
      final Member member = existingMember.get();
      final StreamAttendee streamAttendee = createStreamAttendee(stream, FleenUser.of(member.getMemberId()));
      approveAttendeeRequestAndSetOrganizerComment(streamAttendee, addNewEventAttendeeDto.getComment());
      streamAttendeeRepository.save(streamAttendee);
    }
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
   * @return an EventAttendeesResponse DTO containing the list of attendees for the event
   * @throws FleenStreamNotFoundException if the event with the specified eventId is not found
   */
  @Override
  public EventAttendeesResponse getEventAttendees(final Long eventId) {
    final FleenStream stream = fleenStreamRepository.findById(eventId)
        .orElseThrow(() -> new FleenStreamNotFoundException(eventId));

    final EventAttendeesResponse eventAttendeesResponse = getAttendees(stream.getAttendees());
    eventAttendeesResponse.setEventId(eventId);

    return eventAttendeesResponse;
  }

  /**
   * Retrieves the list of attendees for a given event and converts them to an EventAttendeesResponse DTO.
   *
   * <p>If the provided set of attendees is not null and not empty, it converts each StreamAttendee entity to
   * an EventAttendeeResponse DTO and sets them in the response object.</p>
   *
   * <p>If no attendees are found (either the set is null or empty), it returns an empty EventAttendeesResponse object.</p>
   *
   * @param attendees the set of StreamAttendee entities representing the attendees of the event
   * @return an EventAttendeesResponse DTO containing the list of attendees, or an empty EventAttendeesResponse if there are no attendees
   */
  public EventAttendeesResponse getAttendees(final Set<StreamAttendee> attendees) {
    final EventAttendeesResponse eventAttendeesResponse = EventAttendeesResponse.of();
    // Check if the attendees list is not empty and set it to the list of attendees in the response
    if (nonNull(attendees) && !attendees.isEmpty()) {
      final List<EventAttendeeResponse> attendeesResponses = StreamAttendeeMapper.toEventAttendeeResponses(new ArrayList<>(attendees));
      eventAttendeesResponse.setAttendees(attendeesResponses);
    }
    return eventAttendeesResponse;
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
   * Validates if the specified user is the creator of the given event.
   *
   * <p>This method checks if the user ID associated with the event creator matches
   * the ID of the user trying to perform an action on the event. If the IDs do not
   * match, a FleenStreamNotCreatedByUserException is thrown.</p>
   *
   * @param stream the FleenStream representing the event to validate
   * @param user the user attempting to perform an action on the event
   * @throws UnableToCompleteOperationException if one of the input is invalid
   * @throws FleenStreamNotCreatedByUserException if the event was not created by the specified user
   */
  public void validateCreatorOfEvent(final FleenStream stream, final FleenUser user) {
    // Throw an exception if the any of the provided values is null
    checkIsNullAny(Set.of(stream, user), UnableToCompleteOperationException::new);

    // Check if the event creator's ID matches the user's ID
    boolean isSame = Objects.equals(stream.getMember().getMemberId(), user.getId());
    if (!isSame) {
      throw new FleenStreamNotCreatedByUserException(user.getId());
    }
  }

  /**
   * Verifies if the stream end date is in the future.
   *
   * <p>This method checks if the provided stream end date is before the current date and time.
   * If the stream end date is in the past, it throws a FleenStreamNotCreatedByUserException with the end date as a message.</p>
   *
   * @param streamEndDate the end date and time of the stream to verify
   * @throws UnableToCompleteOperationException if one of the input is invalid
   * @throws StreamAlreadyHappenedException if the stream end date is in the past
   */
  public void verifyStreamEndDate(final LocalDateTime streamEndDate) {
    // Throw an exception if the provided end date is null
    checkIsNull(streamEndDate, UnableToCompleteOperationException::new);

    if (streamEndDate.isBefore(LocalDateTime.now())) {
      throw new StreamAlreadyHappenedException(streamEndDate.toString());
    }
  }

  /**
   * Checks if a user is already an attendee of the given stream and throws an exception if they are.
   *
   * <p>This method is used to ensure that a user cannot request to join a stream if they are already an attendee.
   * It checks the list of attendees in the provided stream for the given user ID. <p>
   *
   * <p>If the user is found as an attendee, an {@link AlreadyRequestedToJoinStreamException} is thrown
   * with the attendee's request to join status.</p>
   *
   * @param stream the {@link FleenStream} to check for existing attendees
   * @param userId the ID of the user to check for
   * @throws UnableToCompleteOperationException if one of the input is invalid
   * @throws AlreadyRequestedToJoinStreamException if the user already requested to join the stream or is already an attendee of the stream
   */
  public void checkIfUserIsAlreadyAnAttendeeAndThrowError(final FleenStream stream, final Long userId) {
    // Throw an exception if the any of the provided values is null
    checkIsNullAny(Set.of(stream, userId), UnableToCompleteOperationException::new);

    // If the user is found as an attendee, throw an exception with the attendee's request to join status
    final Optional<StreamAttendee> existingStreamAttendee = checkIfUserIsAlreadyAnAttendee(stream, userId);

    // If the user is found as an attendee, throw an exception with the attendee's request to join status
    if (existingStreamAttendee.isPresent()) {
      final StreamAttendee streamAttendee = existingStreamAttendee.get();
      throw new AlreadyRequestedToJoinStreamException(streamAttendee.getStreamAttendeeRequestToJoinStatus().getValue());
    }
  }

  /**
   * Checks if the given stream is private, and if so, throws a {@link CannotJointStreamWithoutApprovalException}.
   *
   * @param eventId the ID of the event associated with the stream
   * @param stream  the {@link FleenStream} object to check for privacy
   * @throws CannotJointStreamWithoutApprovalException if the stream's visibility is set to PRIVATE
   */
  protected void checkIfStreamIsPrivate(final Long eventId, final FleenStream stream) {
    if (stream.getStreamVisibility() == PRIVATE) {
      throw new CannotJointStreamWithoutApprovalException(eventId);
    }
  }

  /**
   * Checks if the user is already an attendee of the given stream.
   *
   * <p>This method checks if the user is already an attendee of the specified stream by filtering the list of attendees.
   * If the user is found in the list, it throws a FleenStreamNotCreatedByUserException with the attendee's request to join status as a message.</p>
   *
   * @param stream the stream to check for the user's attendance
   * @param userId the user's ID to check
   * @return Optional that may contain the user found as an attendee
   * @throws UnableToCompleteOperationException if one of the input is invalid
   */
  public Optional<StreamAttendee> checkIfUserIsAlreadyAnAttendee(final FleenStream stream, final Long userId) {
    // Throw an exception if the any of the provided values is null
    checkIsNullAny(Set.of(stream, userId), UnableToCompleteOperationException::new);
    // Find if the user is already an attendee of the stream
    return stream.getAttendees()
        .stream()
        .filter(attendee -> userId.equals(attendee.getMember().getMemberId()))
        .findAny();
  }

  /**
   * Creates a new StreamAttendee for the specified stream and user.
   *
   * <p>This method creates a new StreamAttendee object for a given stream and user, setting the request to join status to approved.</p>
   *
   * @param stream the stream to be joined
   * @param user the user requesting to join the stream
   * @return the created StreamAttendee
   * @throws UnableToCompleteOperationException if one of the input is invalid
   */
  public StreamAttendee createStreamAttendee(final FleenStream stream, final FleenUser user) {
    // Throw an exception if the any of the provided values is null
    checkIsNullAny(Set.of(stream, user), UnableToCompleteOperationException::new);

    return StreamAttendee.of(user.toMember(), stream);
  }

  /**
   * Creates a new {@link StreamAttendee} with an additional comment for the given stream and user.
   *
   * <p>This method first creates a {@link StreamAttendee} by calling the {@link #createStreamAttendee(FleenStream, FleenUser)}
   * method. It then sets the provided comment on the newly created {@link StreamAttendee} before returning it.</p>
   *
   * <p>The method is useful for cases where an attendee needs to be created with an additional comment
   * indicating some special information or note regarding their attendance.</p>
   *
   * @param stream the {@link FleenStream} to which the attendee is to be added
   * @param user the {@link FleenUser} who is being added as an attendee
   * @param comment the comment to be added to the {@link StreamAttendee}
   * @return the newly created {@link StreamAttendee} with the added comment
   * @throws UnableToCompleteOperationException if one of the input is invalid
   */
  public StreamAttendee createStreamAttendeeWithComment(final FleenStream stream, final FleenUser user, final String comment) {
    // Throw an exception if the any of the provided values is null
    checkIsNullAny(Set.of(stream, user), UnableToCompleteOperationException::new);

    final StreamAttendee streamAttendee = createStreamAttendee(stream, user);
    streamAttendee.setAttendeeComment(comment);
    return streamAttendee;
  }

  /**
   * Checks if an event is active and not cancelled.
   *
   * <p>This method verifies whether a given FleenStream event is active and not in the cancelled status.
   * It returns true if the event is active, and false otherwise.</p>
   *
   * <p>The method first checks if the provided FleenStream object is not null.
   * If the object is null, it returns false.
   * If the object is not null, it checks the stream status and returns true if the status is not CANCELLED.</p>
   *
   * @param stream the FleenStream event to check
   * @throws UnableToCompleteOperationException if one of the input is invalid
   * @throws StreamAlreadyCancelledException if the stream or event has been cancelled
   */
  public void verifyEventIsNotCancelled(final FleenStream stream) {
    // Throw an exception if the provided stream is null
    checkIsNull(stream, UnableToCompleteOperationException::new);

    if (stream.getStreamStatus() == CANCELLED) {
      throw new StreamAlreadyCancelledException(stream.getFleenStreamId());
    }
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
   * Verifies the details of a given stream to ensure it is valid for further processing.
   *
   * <p>This method performs several checks on the provided stream: it validates if the user is the creator of the event,
   * checks if the stream's scheduled end date has not passed, and verifies that the event is not cancelled.</p>
   *
   * @param stream the FleenStream to be verified
   * @param user the FleenUser to be validated as the creator of the event
   */
  public void verifyStreamDetails(final FleenStream stream, final FleenUser user) {
    // Validate if the user is the creator of the event
    validateCreatorOfEvent(stream, user);
    // Check if the stream is still active and can be joined.
    verifyStreamEndDate(stream.getScheduledEndDate());
    // Verify the event is not cancelled
    verifyEventIsNotCancelled(stream);
  }
}
