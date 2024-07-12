package com.fleencorp.feen.service.impl.stream;

import com.fleencorp.base.model.view.search.SearchResultView;
import com.fleencorp.feen.constant.stream.StreamTimeType;
import com.fleencorp.feen.constant.stream.StreamVisibility;
import com.fleencorp.feen.event.publisher.StreamEventPublisher;
import com.fleencorp.feen.exception.calendar.CalendarNotFoundException;
import com.fleencorp.feen.exception.stream.*;
import com.fleencorp.feen.model.domain.calendar.Calendar;
import com.fleencorp.feen.model.domain.stream.FleenStream;
import com.fleencorp.feen.model.domain.stream.StreamAttendee;
import com.fleencorp.feen.model.domain.user.Member;
import com.fleencorp.feen.model.dto.event.*;
import com.fleencorp.feen.model.dto.event.CreateCalendarEventDto.EventAttendeeOrGuest;
import com.fleencorp.feen.model.event.AddCalendarEventAttendeesEvent;
import com.fleencorp.feen.model.request.calendar.event.*;
import com.fleencorp.feen.model.request.search.calendar.CalendarEventSearchRequest;
import com.fleencorp.feen.model.response.base.FleenFeenResponse;
import com.fleencorp.feen.model.response.base.FleenStreamResponse;
import com.fleencorp.feen.model.response.event.*;
import com.fleencorp.feen.model.response.external.google.calendar.event.*;
import com.fleencorp.feen.model.security.FleenUser;
import com.fleencorp.feen.repository.calendar.CalendarRepository;
import com.fleencorp.feen.repository.stream.FleenStreamRepository;
import com.fleencorp.feen.repository.stream.StreamAttendeeRepository;
import com.fleencorp.feen.repository.stream.UserFleenStreamRepository;
import com.fleencorp.feen.repository.user.MemberRepository;
import com.fleencorp.feen.service.external.google.calendar.GoogleCalendarEventService;
import com.fleencorp.feen.service.stream.EventService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
import static com.fleencorp.feen.mapper.StreamAttendeeMapper.toEventAttendeeResponse;
import static java.util.Objects.isNull;
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
  private final GoogleCalendarEventService googleCalendarEventService;
  private final FleenStreamRepository fleenStreamRepository;
  private final UserFleenStreamRepository userFleenStreamRepository;
  private final CalendarRepository calendarRepository;
  private final StreamAttendeeRepository streamAttendeeRepository;
  private final MemberRepository memberRepository;
  private final StreamEventPublisher streamEventPublisher;

  /**
  * Constructor for the EventServiceImpl class.
  *
  * <p>Initializes an instance of EventServiceImpl with the necessary dependencies,
  * including Google Calendar event service, FleenStream repository, and Calendar repository.</p>
  *
  * @param delegatedAuthorityEmail the email address used for delegated authority
  * @param googleCalendarEventService the service for interacting with Google Calendar events
  * @param fleenStreamRepository the repository for FleenStream operations
  * @param userFleenStreamRepository  the repository for FleenStream operations related to a user profile
  * @param calendarRepository the repository for calendar operations
  * @param streamAttendeeRepository the repository for managing event or stream attendees
  * @param memberRepository the repository for managing users
  * @param streamEventPublisher the service for publishing stream or event related actions
  */
  public EventServiceImpl(
      @Value("${google.delegated.authority.email}") String delegatedAuthorityEmail,
      GoogleCalendarEventService googleCalendarEventService,
      FleenStreamRepository fleenStreamRepository,
      UserFleenStreamRepository userFleenStreamRepository,
      CalendarRepository calendarRepository,
      StreamAttendeeRepository streamAttendeeRepository,
      MemberRepository memberRepository,
      StreamEventPublisher streamEventPublisher) {
    this.delegatedAuthorityEmail = delegatedAuthorityEmail;
    this.googleCalendarEventService = googleCalendarEventService;
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
  public SearchResultView findEvents(CalendarEventSearchRequest searchRequest) {
    Page<FleenStream> page;
    if (areNotEmpty(searchRequest.getStartDate(), searchRequest.getEndDate())) {
      page = fleenStreamRepository.findByDateBetween(searchRequest.getStartDate().atStartOfDay(), searchRequest.getEndDate().atStartOfDay(), searchRequest.getPage());
    } else if (nonNull(searchRequest.getTitle())) {
      page = fleenStreamRepository.findByTitle(searchRequest.getTitle(), searchRequest.getPage());
    } else {
      page = fleenStreamRepository.findMany(searchRequest.getPage());
    }

    List<FleenStreamResponse> views = toFleenStreams(page.getContent());
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
  public SearchResultView findEvents(CalendarEventSearchRequest searchRequest, StreamTimeType streamTimeType) {
    Page<FleenStream> page;
    if (streamTimeType == StreamTimeType.UPCOMING) {
      page = getUpcomingEvents(searchRequest);
    } else if (streamTimeType == StreamTimeType.PAST) {
      page = getPastEvents(searchRequest);
    } else {
      page = getLiveEvents(searchRequest);
    }

    List<FleenStreamResponse> views = toFleenStreams(page.getContent());
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
  private Page<FleenStream> getUpcomingEvents(CalendarEventSearchRequest searchRequest) {
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
  private Page<FleenStream> getPastEvents(CalendarEventSearchRequest searchRequest) {
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
  private Page<FleenStream> getLiveEvents(CalendarEventSearchRequest searchRequest) {
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
  public SearchResultView findEvents(CalendarEventSearchRequest searchRequest, FleenUser user) {
    Page<FleenStream> page;
    StreamVisibility streamVisibility = searchRequest.getVisibility(PUBLIC);

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

    List<FleenStreamResponse> views = toFleenStreams(page.getContent());
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
  public SearchResultView findEventsAttendedByUser(CalendarEventSearchRequest searchRequest, FleenUser user) {
    Page<FleenStream> page;
    if (areNotEmpty(searchRequest.getStartDate(), searchRequest.getEndDate())) {
      page = userFleenStreamRepository.findAttendedByDateBetweenAndUser(searchRequest.getStartDate().atStartOfDay(), searchRequest.getEndDate().atStartOfDay(),
              user.toMember(), searchRequest.getPage());
    } else if (nonNull(searchRequest.getTitle())) {
      page = userFleenStreamRepository.findAttendedByTitleAndUser(searchRequest.getTitle(), user.toMember(), searchRequest.getPage());
    } else {
      page = userFleenStreamRepository.findAttendedByUser(user.toMember(), searchRequest.getPage());
    }

    List<FleenStreamResponse> views = toFleenStreams(page.getContent());
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
  public SearchResultView findEventsAttendedWithAnotherUser(CalendarEventSearchRequest searchRequest, FleenUser user) {
    Page<FleenStream> page;
    // Retrieve events attended together by the current user and another user
    if (nonNull(searchRequest.getAnotherUserId())) {
      page = userFleenStreamRepository.findEventsAttendedTogether(user.toMember(), Member.of(searchRequest.getAnotherUserId()), searchRequest.getPage());
    } else {
      page = new PageImpl<>(List.of());
    }

    List<FleenStreamResponse> views = toFleenStreams(page.getContent());
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
  public RetrieveEventResponse retrieveEvent(Long eventId) {
    FleenStream stream = fleenStreamRepository.findById(eventId)
            .orElseThrow(() -> new FleenStreamNotFoundException(eventId));
    return RetrieveEventResponse.of(eventId, toFleenStreamResponse(stream));
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
  public CreateEventResponse createEvent(CreateCalendarEventDto createCalendarEventDto, FleenUser user) {
    Calendar calendar = calendarRepository.findDistinctByCodeIgnoreCase(user.getCountry())
            .orElseThrow(() -> new CalendarNotFoundException(user.getCountry()));

    CreateCalendarEventRequest createCalendarEventRequest = CreateCalendarEventRequest.by(createCalendarEventDto);

    // Set event organizer as attendee in Google calendar
    EventAttendeeOrGuest eventAttendeeOrGuest = new EventAttendeeOrGuest();
    eventAttendeeOrGuest.setEmailAddress(user.getEmailAddress());
    eventAttendeeOrGuest.setAliasOrDisplayName(user.getFullName());
    eventAttendeeOrGuest.setIsOrganizer(true);

    // Add event organizer as an attendee
    createCalendarEventRequest.getAttendeeOrGuestEmailAddresses().add(eventAttendeeOrGuest);
    // Update the event request with necessary details
    createCalendarEventRequest.update(createCalendarEventRequest, calendar.getExternalId(), delegatedAuthorityEmail, user.getEmailAddress());
    // Create the event using an external service (Google Calendar)
    GoogleCreateCalendarEventResponse googleCreateCalendarEventResponse = googleCalendarEventService.createEvent(createCalendarEventRequest);

    // Create a FleenStream object from the DTO and update its details with the Google Calendar response
    FleenStream stream = createCalendarEventDto.toFleenStream();
    stream.updateDetails(
      googleCreateCalendarEventResponse.getEventId(),
      googleCreateCalendarEventResponse.getEvent().getHtmlLink(),
      user.getFullName(),
      user.getEmailAddress(),
      user.getPhoneNumber());

    stream = fleenStreamRepository.save(stream);
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
  public CreateEventResponse createInstantEvent(CreateInstantCalendarEventDto createInstantCalendarEventDto, FleenUser user) {
    Calendar calendar = calendarRepository.findDistinctByCodeIgnoreCase(user.getCountry())
            .orElseThrow(() -> new CalendarNotFoundException(user.getCountry()));

    CreateInstantCalendarEventRequest createInstantCalendarEventRequest = CreateInstantCalendarEventRequest.by(createInstantCalendarEventDto);
    // Update the instant event request with necessary details
    createInstantCalendarEventRequest.update(calendar.getExternalId());
    // Create the instant event using an external service (Google Calendar)
    GoogleCreateInstantCalendarEventResponse googleCreateInstantCalendarEventResponse = googleCalendarEventService.createInstantEvent(createInstantCalendarEventRequest);

    // Create a FleenStream object from the DTO and update its details with the Google Calendar response
    FleenStream stream = createInstantCalendarEventDto.toFleenStream();
    stream.updateDetails(
      googleCreateInstantCalendarEventResponse.getEventId(),
      googleCreateInstantCalendarEventResponse.getEvent().getHtmlLink(),
      user.getFullName(),
      user.getEmailAddress(),
      user.getPhoneNumber());

    stream = fleenStreamRepository.save(stream);
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
  public UpdateEventResponse updateEvent(Long eventId, UpdateCalendarEventDto updateCalendarEventDto, FleenUser user) {
    Calendar calendar = calendarRepository.findDistinctByCodeIgnoreCase(user.getCountry())
            .orElseThrow(() -> new CalendarNotFoundException(user.getCountry()));

    FleenStream stream = fleenStreamRepository.findById(eventId)
            .orElseThrow(() -> new FleenStreamNotFoundException(eventId));

    // Validate if the user is the creator of the event
    verifyStreamDetails(stream, user);

    // Prepare a request to patch the calendar event with updated details
    PatchCalendarEventRequest patchCalendarEventRequest = PatchCalendarEventRequest
      .of(calendar.getExternalId(),
          stream.getExternalId(),
          updateCalendarEventDto.getTitle(),
          updateCalendarEventDto.getDescription());
    // Patch the event using an external service (Google Calendar)
    GooglePatchCalendarEventResponse googlePatchCalendarEventResponse = googleCalendarEventService.patchEvent(patchCalendarEventRequest);
    log.info("Event updated: {}", googlePatchCalendarEventResponse);

    // Update the FleenStream object with the response from Google Calendar
    stream.setExternalId(googlePatchCalendarEventResponse.getEventId());
    stream.setStreamLink(googlePatchCalendarEventResponse.getEvent().getHtmlLink());
    stream.update(
            updateCalendarEventDto.getTitle(),
            updateCalendarEventDto.getDescription(),
            updateCalendarEventDto.getTags(),
            updateCalendarEventDto.getLocation());
    stream = fleenStreamRepository.save(stream);

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
  public DeleteEventResponse deleteEvent(Long eventId, FleenUser user) {
    Calendar calendar = calendarRepository.findDistinctByCodeIgnoreCase(user.getCountry())
            .orElseThrow(() -> new CalendarNotFoundException(user.getCountry()));

    FleenStream stream = fleenStreamRepository.findById(eventId)
            .orElseThrow(() -> new FleenStreamNotFoundException(eventId));

    // Validate if the user is the creator of the event
    validateCreatorOfEvent(stream, user);

    // Create a request to delete the calendar event
    DeleteCalendarEventRequest deleteCalendarEventRequest = DeleteCalendarEventRequest
      .of(calendar.getExternalId(),
          stream.getExternalId());

    // Delete the event using an external service (Google Calendar)
    GoogleDeleteCalendarEventResponse googleDeleteCalendarEventResponse = googleCalendarEventService.deleteEvent(deleteCalendarEventRequest);
    log.info("Deleted event: {}", googleDeleteCalendarEventResponse.getEventId());

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
  public CancelEventResponse cancelEvent(Long eventId, FleenUser user) {
    Calendar calendar = calendarRepository.findDistinctByCodeIgnoreCase(user.getCountry())
            .orElseThrow(() -> new CalendarNotFoundException(user.getCountry()));

    FleenStream stream = fleenStreamRepository.findById(eventId)
            .orElseThrow(() -> new FleenStreamNotFoundException(eventId));

    // Verify stream details like the owner, event date and active status of the event
    verifyStreamDetails(stream, user);

    // Create a request to cancel the calendar event
    CancelCalendarEventRequest cancelCalendarEventRequest = CancelCalendarEventRequest
      .of(calendar.getExternalId(),
          stream.getExternalId());

    // Cancel the event using an external service (Google Calendar)
    GoogleCancelCalendarEventResponse googleCancelCalendarEventResponse = googleCalendarEventService.cancelEvent(cancelCalendarEventRequest);
    log.info("Cancelled event: {}", googleCancelCalendarEventResponse.getEventId());

    stream.setStreamStatus(CANCELLED);
    fleenStreamRepository.save(stream);

    return CancelEventResponse.of(eventId);
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
  public RescheduleEventResponse rescheduleEvent(Long eventId, RescheduleCalendarEventDto rescheduleCalendarEventDto, FleenUser user) {
    Calendar calendar = calendarRepository.findDistinctByCodeIgnoreCase(user.getCountry())
            .orElseThrow(() -> new CalendarNotFoundException(user.getCountry()));

    FleenStream stream = fleenStreamRepository.findById(eventId)
            .orElseThrow(() -> new FleenStreamNotFoundException(eventId));

    // Verify stream details like the owner, event date and active status of the event
    verifyStreamDetails(stream, user);

    // Prepare a request to reschedule the calendar event with the new schedule details
    RescheduleCalendarEventRequest rescheduleCalendarEventRequest = RescheduleCalendarEventRequest
      .of(calendar.getExternalId(),
          stream.getExternalId(),
          rescheduleCalendarEventDto.getStartDateTime(),
          rescheduleCalendarEventDto.getEndDateTime(),
          rescheduleCalendarEventDto.getTimezone());

    // Reschedule the event using an external service (Google Calendar)
    GoogleRescheduleCalendarEventResponse googleRescheduleCalendarEventResponse =  googleCalendarEventService.rescheduleEvent(rescheduleCalendarEventRequest);
    log.info("Rescheduled event: {}", googleRescheduleCalendarEventResponse.getEventId());

    stream.updateSchedule(rescheduleCalendarEventDto.getStartDateTime(), rescheduleCalendarEventDto.getEndDateTime(), rescheduleCalendarEventDto.getTimezone());
    fleenStreamRepository.save(stream);

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
  public FleenFeenResponse joinEvent(Long eventId, FleenUser user) {
    Calendar calendar = calendarRepository.findDistinctByCodeIgnoreCase(user.getCountry())
            .orElseThrow(() -> new CalendarNotFoundException(user.getCountry()));

    FleenStream stream = fleenStreamRepository.findById(eventId)
            .orElseThrow(() -> new FleenStreamNotFoundException(eventId));

    // Verify event is not canceled
    verifyEventIsNotCancelled(stream);
    // Check if the stream is still active and can be joined.
    verifyStreamEndDate(stream.getScheduledEndDate());

    if (stream.getStreamVisibility() == PRIVATE) {
      throw new CannotJointStreamWithoutApprovalException(eventId);
    }

    // CHeck if the user is already an attendee
    checkIfUserIsAlreadyAnAttendeeAndThrowError(stream, user.getId());

    // Create a request to add the user as an attendee to the calendar event
    AddNewEventAttendeeRequest addNewEventAttendeeRequest = AddNewEventAttendeeRequest
            .of(calendar.getExternalId(),
                stream.getExternalId(),
                user.getEmailAddress());

    // Add the user as an attendee to the event using an external service (Google Calendar)
    GoogleAddNewCalendarEventAttendeeResponse googleAddNewCalendarEventAttendeeResponse = googleCalendarEventService.addNewAttendeeToCalendarEvent(addNewEventAttendeeRequest);
    log.info("Attendee join event: {}", googleAddNewCalendarEventAttendeeResponse.getEventId());

    // Create a new StreamAttendee entry for the user
    StreamAttendee streamAttendee = createStreamAttendee(stream, user);
    streamAttendee.setStreamAttendeeRequestToJoinStatus(APPROVED);

    // Add the new StreamAttendee to the event's attendees list and save
    stream.getAttendees().add(streamAttendee);
    fleenStreamRepository.save(stream);
    streamAttendeeRepository.save(streamAttendee);

    return new FleenFeenResponse(eventId);
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
  public RequestToJoinEventResponse requestToJoinEvent(Long eventId, RequestToJoinEventDto requestToJoinEventDto, FleenUser user) {
    FleenStream stream = fleenStreamRepository.findById(eventId)
            .orElseThrow(() -> new FleenStreamNotFoundException(eventId));

    // Check if the stream is still active and can be joined.
    verifyStreamEndDate(stream.getScheduledEndDate());
    // Check if event is not cancelled
    verifyEventIsNotCancelled(stream);
    // CHeck if the user is already an attendee
    checkIfUserIsAlreadyAnAttendeeAndThrowError(stream, user.getId());

    // Create a new StreamAttendee entry for the user
    StreamAttendee streamAttendee = createStreamAttendeeWithComment(stream, user, requestToJoinEventDto.getComment());

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
  public ProcessAttendeeRequestToJoinEventResponse processAttendeeRequestToJoinEvent(Long eventId, ProcessAttendeeRequestToJoinEventDto processAttendeeRequestToJoinEventDto, FleenUser user) {
    FleenStream stream = fleenStreamRepository.findById(eventId)
            .orElseThrow(() -> new FleenStreamNotFoundException(eventId));

    // Verify stream details like the owner, event date and active status of the event
    verifyStreamDetails(stream, user);

    Optional<StreamAttendee> existingStreamAttendee = checkIfUserIsAlreadyAnAttendee(stream, Long.parseLong(processAttendeeRequestToJoinEventDto.getAttendeeUserId()));
    // Check if the attendee has submitted a request and the request is still pending
    if (existingStreamAttendee.isPresent() && existingStreamAttendee.get().getStreamAttendeeRequestToJoinStatus() == PENDING) {
      StreamAttendee streamAttendee = existingStreamAttendee.get();
      streamAttendee.setStreamAttendeeRequestToJoinStatus(processAttendeeRequestToJoinEventDto.getActualJoinStatus());
      streamAttendee.setOrganizerComment(processAttendeeRequestToJoinEventDto.getComment());

      if (processAttendeeRequestToJoinEventDto.getActualJoinStatus() == APPROVED) {
        Calendar calendar = calendarRepository.findDistinctByCodeIgnoreCase(user.getCountry())
                .orElseThrow(() -> new CalendarNotFoundException(user.getCountry()));
        // Add attendee to the event by invitation
        addAttendeeToEvent(calendar.getExternalId(), stream.getExternalId(), streamAttendee.getMember().getEmailAddress(), null);

        streamAttendeeRepository.save(streamAttendee);
        fleenStreamRepository.save(stream);
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
  public UpdateEventVisibilityResponse updateEventVisibility(Long eventId, UpdateEventVisibilityDto updateEventVisibilityDto, FleenUser user) {
    Calendar calendar = calendarRepository.findDistinctByCodeIgnoreCase(user.getCountry())
            .orElseThrow(() -> new CalendarNotFoundException(user.getCountry()));

    FleenStream stream = fleenStreamRepository.findById(eventId)
            .orElseThrow(() -> new FleenStreamNotFoundException(eventId));

    StreamVisibility currentStreamVisibility = stream.getStreamVisibility();

    // Verify stream details like the owner, event date and active status of the event
    verifyStreamDetails(stream, user);

    // Create a request to update the event's visibility
    UpdateCalendarEventVisibilityRequest updateCalendarEventVisibilityRequest = UpdateCalendarEventVisibilityRequest
      .of(calendar.getExternalId(),
          stream.getExternalId(),
          updateEventVisibilityDto.getVisibility());

    // Send the request to the Google Calendar service
    GooglePatchCalendarEventResponse googlePatchCalendarEventResponse = googleCalendarEventService.updateEventVisibility(updateCalendarEventVisibilityRequest);
    log.info("Updated event visibility: {}", googlePatchCalendarEventResponse);

    stream.setStreamVisibility(updateEventVisibilityDto.getActualVisibility());
    fleenStreamRepository.save(stream);

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
  public void sendInvitationToPendingAttendeesBasedOnCurrentStreamStatus(String calendarExternalId, FleenStream stream, StreamVisibility previousStreamVisibility) {
    if (isNull(stream) || isNull(previousStreamVisibility)) {
      throw new UnableToCompleteOperationException();
    }

    // Determine the updated or current visibility of the stream
    StreamVisibility currentStreamVisibility = stream.getStreamVisibility();

    // If the stream visibility is PUBLIC, and it was previously PRIVATE or PROTECTED
    if (currentStreamVisibility == PUBLIC && (previousStreamVisibility == PRIVATE || previousStreamVisibility == PROTECTED)) {
      // Retrieve all pending attendees for the stream
      List<StreamAttendee> streamAttendees = streamAttendeeRepository.findAllByFleenStreamAndStreamAttendeeRequestToJoinStatus(stream, PENDING);

      Set<String> attendeesOrGuestsEmailAddresses = getAttendeesEmailAddresses(streamAttendees);

      // Create an event to add new attendees and publish it
      AddCalendarEventAttendeesEvent addCalendarEventAttendeesEvent = AddCalendarEventAttendeesEvent
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
  public Set<String> getAttendeesEmailAddresses(List<StreamAttendee> streamAttendees) {
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
  public AddNewEventAttendeeResponse addEventAttendee(Long eventId, AddNewEventAttendeeDto addNewEventAttendeeDto, FleenUser user) {
    Calendar calendar = calendarRepository.findDistinctByCodeIgnoreCase(user.getCountry())
            .orElseThrow(() -> new CalendarNotFoundException(user.getCountry()));

    FleenStream stream = fleenStreamRepository.findById(eventId)
            .orElseThrow(() -> new FleenStreamNotFoundException(eventId));

    // Verify stream details like the owner, event date and active status of the event
    verifyStreamDetails(stream, user);

    Optional<StreamAttendee> existingStreamAttendee = streamAttendeeRepository.findDistinctByEmail(addNewEventAttendeeDto.getEmailAddress());
    if (existingStreamAttendee.isPresent() &&
            (existingStreamAttendee.get().getStreamAttendeeRequestToJoinStatus() == PENDING ||
                    existingStreamAttendee.get().getStreamAttendeeRequestToJoinStatus() == DISAPPROVED)) {
      StreamAttendee streamAttendee = existingStreamAttendee.get();
      streamAttendee.setStreamAttendeeRequestToJoinStatus(APPROVED);
      streamAttendee.setOrganizerComment(addNewEventAttendeeDto.getComment());

      // Add attendee to the event using an external service
      addAttendeeToEvent(calendar.getExternalId(), stream.getExternalId(), addNewEventAttendeeDto.getEmailAddress(), addNewEventAttendeeDto.getAliasOrDisplayName());
      streamAttendeeRepository.save(streamAttendee);
    } else {
      // Check if there is a member with attendee's email address from the request and add them as a stream attendee
      Optional<Member> existingMember = memberRepository.findByEmailAddress(addNewEventAttendeeDto.getEmailAddress());
      if (existingMember.isPresent()) {
        Member member = existingMember.get();
        StreamAttendee streamAttendee = createStreamAttendee(stream, FleenUser.of(member.getMemberId()));
        streamAttendee.setStreamAttendeeRequestToJoinStatus(APPROVED);
        streamAttendee.setOrganizerComment(addNewEventAttendeeDto.getComment());
        streamAttendeeRepository.save(streamAttendee);
      }
      // Add attendee to the event using an external service
      addAttendeeToEvent(calendar.getExternalId(), stream.getExternalId(), addNewEventAttendeeDto.getEmailAddress(), addNewEventAttendeeDto.getAliasOrDisplayName());
    }

    fleenStreamRepository.save(stream);
    return AddNewEventAttendeeResponse.of(eventId, toEventResponse(stream), addNewEventAttendeeDto.getEmailAddress());
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
  public EventAttendeesResponse getEventAttendees(Long eventId) {
    FleenStream stream = fleenStreamRepository.findById(eventId)
            .orElseThrow(() -> new FleenStreamNotFoundException(eventId));

    EventAttendeesResponse eventAttendeesResponse = getAttendees(stream.getAttendees());
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
  public EventAttendeesResponse getAttendees(Set<StreamAttendee> attendees) {
    EventAttendeesResponse eventAttendeesResponse = EventAttendeesResponse.builder()
            .build();
    if (nonNull(attendees) && !attendees.isEmpty()) {
      List<EventAttendeeResponse> attendeesResponses = toEventAttendeeResponse(new ArrayList<>(attendees));
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
  public TotalEventsCreatedByUserResponse countTotalEventsByUser(FleenUser user) {
    Long totalCount = userFleenStreamRepository.countTotalEventsByUser(user.toMember());
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
  public TotalEventsAttendedByUserResponse countTotalEventsAttended(FleenUser user) {
    Long totalCount = userFleenStreamRepository.countTotalEventsAttended(user.toMember());
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
  public void validateCreatorOfEvent(FleenStream stream, FleenUser user) {
    if (isNull(stream) || isNull(user)) {
      throw new UnableToCompleteOperationException();
    }

    // Check if the event creator's ID matches the user's ID
    if (!Objects.equals(stream.getMember().getMemberId(), user.getId())) {
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
  public void verifyStreamEndDate(LocalDateTime streamEndDate) {
    if (isNull(streamEndDate)) {
      throw new UnableToCompleteOperationException();
    }

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
  public void checkIfUserIsAlreadyAnAttendeeAndThrowError(FleenStream stream, Long userId) {
    if (isNull(stream) || isNull(userId)) {
      throw new UnableToCompleteOperationException();
    }

    // If the user is found as an attendee, throw an exception with the attendee's request to join status
    Optional<StreamAttendee> existingStreamAttendee = checkIfUserIsAlreadyAnAttendee(stream, userId);

    // If the user is found as an attendee, throw an exception with the attendee's request to join status
    if (existingStreamAttendee.isPresent()) {
      StreamAttendee streamAttendee = existingStreamAttendee.get();
      throw new AlreadyRequestedToJoinStreamException(streamAttendee.getStreamAttendeeRequestToJoinStatus().getValue());
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
  public Optional<StreamAttendee> checkIfUserIsAlreadyAnAttendee(FleenStream stream, Long userId) {
    if (isNull(stream) || isNull(userId)) {
      throw new UnableToCompleteOperationException();
    }
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
  * @param fleenStream the stream to be joined
  * @param user the user requesting to join the stream
  * @return the created StreamAttendee
  * @throws UnableToCompleteOperationException if one of the input is invalid
  */
  public StreamAttendee createStreamAttendee(FleenStream fleenStream, FleenUser user) {
    if (isNull(fleenStream) || isNull(user)) {
      throw new UnableToCompleteOperationException();
    }

    return StreamAttendee.builder()
            .member(user.toMember())
            .fleenStream(fleenStream)
            .build();
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
  * @param fleenStream the {@link FleenStream} to which the attendee is to be added
  * @param user the {@link FleenUser} who is being added as an attendee
  * @param comment the comment to be added to the {@link StreamAttendee}
  * @return the newly created {@link StreamAttendee} with the added comment
  * @throws UnableToCompleteOperationException if one of the input is invalid
  */
  public StreamAttendee createStreamAttendeeWithComment(FleenStream fleenStream, FleenUser user, String comment) {
    if (isNull(fleenStream) || isNull(user)) {
      throw new UnableToCompleteOperationException();
    }

    StreamAttendee streamAttendee = createStreamAttendee(fleenStream, user);
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
  * @param fleenStream the FleenStream event to check
  * @throws UnableToCompleteOperationException if one of the input is invalid
  * @throws StreamAlreadyCancelledException if the stream or event has been cancelled
  */
  public void verifyEventIsNotCancelled(FleenStream fleenStream) {
    if (isNull(fleenStream)) {
      throw new UnableToCompleteOperationException();
    }

    if (fleenStream.getStreamStatus() == CANCELLED) {
      throw new StreamAlreadyCancelledException(fleenStream.getFleenStreamId());
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
  public void addAttendeeToEvent(String calendarExternalId, String streamExternalId, String attendeeEmailAddress, String displayOrAliasName) {
    // Create a request to add the user as an attendee to the calendar event
    AddNewEventAttendeeRequest addNewEventAttendeeRequest = AddNewEventAttendeeRequest
            .of(calendarExternalId,
                streamExternalId,
                attendeeEmailAddress,
                displayOrAliasName);

    // Add the user as an attendee to the event using an external service (Google Calendar)
    GoogleAddNewCalendarEventAttendeeResponse googleAddNewCalendarEventAttendeeResponse = googleCalendarEventService.addNewAttendeeToCalendarEvent(addNewEventAttendeeRequest);
    log.info("Added attendee to event: {}", googleAddNewCalendarEventAttendeeResponse);
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
  public void verifyStreamDetails(FleenStream stream, FleenUser user) {
    // Validate if the user is the creator of the event
    validateCreatorOfEvent(stream, user);
    // Check if the stream is still active and can be joined.
    verifyStreamEndDate(stream.getScheduledEndDate());
    // Verify the event is not cancelled
    verifyEventIsNotCancelled(stream);
  }
}
