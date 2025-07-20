package com.fleencorp.feen.service.impl.external.google.calendar.attendee;

import com.fleencorp.feen.common.aspect.MeasureExecutionTime;
import com.fleencorp.feen.calendar.model.request.event.create.AddNewEventAttendeeRequest;
import com.fleencorp.feen.calendar.model.request.event.create.AddNewEventAttendeesRequest;
import com.fleencorp.feen.calendar.model.request.event.read.RetrieveCalendarEventRequest;
import com.fleencorp.feen.calendar.model.request.event.update.NotAttendingEventRequest;
import com.fleencorp.feen.common.constant.external.google.calendar.event.EventAttendeeDecisionToJoin;
import com.fleencorp.feen.common.constant.external.google.calendar.event.EventSendUpdate;
import com.fleencorp.feen.common.exception.UnableToCompleteOperationException;
import com.fleencorp.feen.stream.model.dto.event.CreateEventDto;
import com.fleencorp.feen.model.response.external.google.calendar.event.GoogleAddNewCalendarEventAttendeeResponse;
import com.fleencorp.feen.model.response.external.google.calendar.event.GoogleAddNewCalendarEventAttendeesResponse;
import com.fleencorp.feen.model.response.external.google.calendar.event.GoogleRetrieveCalendarEventResponse;
import com.fleencorp.feen.service.external.google.calendar.attendee.GoogleCalendarAttendeeService;
import com.fleencorp.feen.service.external.google.calendar.event.GoogleCalendarEventSearchService;
import com.fleencorp.feen.common.service.report.ReporterService;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static com.fleencorp.feen.common.constant.base.ReportMessageType.GOOGLE_CALENDAR;
import static com.fleencorp.feen.stream.mapper.external.GoogleCalendarEventMapper.mapToEventExpanded;
import static com.fleencorp.feen.common.util.LoggingUtil.logIfEnabled;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * The CalendarService class provides functionality to create events on Google Calendar.
 *
 * <p>This service allows you to interact with the Google Calendar API to create events,
 * manage calendars, and perform other related operations. It abstracts the complexity
 * of direct API calls and provides a simple interface for calendar management.</p>
 *
 * <p>Note: You need to configure your Google API credentials and enable the Calendar API
 * in your Google Cloud project to use this service.</p>
 *
 * <p>Ensure that your application has the necessary permissions and authentication
 * mechanisms in place to interact with the Google Calendar API.</p>
 *
 * <p>Potential exceptions and error handling mechanisms should be implemented to handle
 * API call failures gracefully.</p>
 *
 * <p>This class is part of a larger application that integrates with various Google
 * services for comprehensive calendar management.</p>
 *
 *
 * @see <a href="https://developers.google.com/calendar/api/guides/overview">Google Calendar API overview</a>
 * @see <a href="https://developers.google.com/calendar/api/v3/reference/calendars">Calendars</a>
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
@Service
@Slf4j
public class GoogleCalendarAttendeeServiceImpl implements GoogleCalendarAttendeeService {

  @Getter
  private static final String DEFAULT_CONFERENCE_SOLUTION_NAME = "Google Meet";

  private final Calendar calendar;
  private final GoogleCalendarEventSearchService googleCalendarEventSearchService;
  private final ReporterService reporterService;

  /**
   * Constructs a new CalendarService with the specified Calendar instance.
   *
   * @param calendar  the Calendar instance to be used by this service
   * @param reporterService The service used for reporting events.
   */
  public GoogleCalendarAttendeeServiceImpl(
      final Calendar calendar,
      final GoogleCalendarEventSearchService googleCalendarEventSearchService,
      final ReporterService reporterService) {
    this.calendar = calendar;
    this.googleCalendarEventSearchService = googleCalendarEventSearchService;
    this.reporterService = reporterService;
  }

  /**
   * Adds a new attendee to an existing event on Google Calendar based on the provided request.
   *
   * <p>This method retrieves the specified event from the calendar using its ID,
   * adds a new attendee to the event, and updates the event on the calendar.</p>
   *
   * <p>If an error occurs during the process of adding the attendee, it is logged.</p>
   *
   * @param addNewEventAttendeeRequest the request object containing the calendar ID, event ID, and the new attendee's email address
   * @return {@link GoogleAddNewCalendarEventAttendeeResponse} the response containing the attendee that was added to the event
   * @throws UnableToCompleteOperationException if the operation cannot be completed
   */
  @MeasureExecutionTime
  @Override
  public GoogleAddNewCalendarEventAttendeeResponse addNewAttendeeToCalendarEvent(final AddNewEventAttendeeRequest addNewEventAttendeeRequest) {
    try {
      // Retrieve calendar ID and event ID from the  request
      final String calendarId = addNewEventAttendeeRequest.getCalendarId();
      final String eventId = addNewEventAttendeeRequest.getEventId();

      // Retrieve the event from the calendar
      final Event event = calendar.events()
        .get(calendarId, eventId)
        .execute();

      // If the event exists, add the new attendee and update the event on the calendar
      if (nonNull(event)) {
        final EventAttendee eventAttendee = new EventAttendee();
        // Set attendee basic details including name and email
        updateNewAttendeeBasicInfo(addNewEventAttendeeRequest, eventAttendee);
        // Set the response status to accepted
        eventAttendee.setResponseStatus(EventAttendeeDecisionToJoin.accepted());
        // Create attendee list or register to add attendees
        initializeEventAttendeeList(event);
        // Add attendee to the event
        addAttendee(event, eventAttendee);

        // Send notifications to all attendees if necessary
        calendar.events()
          .update(calendarId, eventId, event)
          .setSendUpdates(EventSendUpdate.all())
          .execute();

        return GoogleAddNewCalendarEventAttendeeResponse.of(
          addNewEventAttendeeRequest.getEventId(), addNewEventAttendeeRequest.getAttendeeEmailAddress(),
          mapToEventExpanded(event)
        );
      }
      logIfEnabled(log::isErrorEnabled, () -> log.error("Cannot add new attendee. Event does not exist or cannot be found. {}", addNewEventAttendeeRequest.getEventId()));
    } catch (final IOException ex) {
      final String errorMessage = String.format("Error has occurred while adding an attendee. Reason: %s", ex.getMessage());
      reporterService.sendMessage(errorMessage, GOOGLE_CALENDAR);
    }
    throw new UnableToCompleteOperationException();
  }

  /**
   * Updates the basic information of a new event attendee based on the provided request data.
   *
   * @param addNewEventAttendeeRequest the request containing details of the new attendee.
   * @param eventAttendee the {@link EventAttendee} object to be updated.
   */
  private static void updateNewAttendeeBasicInfo(final AddNewEventAttendeeRequest addNewEventAttendeeRequest, final EventAttendee eventAttendee) {
    // Check if both the request and the event attendee are not null
    if (nonNull(addNewEventAttendeeRequest) && nonNull(eventAttendee)) {
      // Set the email address of the event attendee
      eventAttendee.setEmail(addNewEventAttendeeRequest.getAttendeeEmailAddress());
      // Set the display name of the event attendee
      eventAttendee.setDisplayName(addNewEventAttendeeRequest.getAttendeeAliasOrDisplayName());
      // Set the response status to accepted
      eventAttendee.setResponseStatus(EventAttendeeDecisionToJoin.accepted());
      // Set any additional comment for the event attendee
      eventAttendee.setComment(addNewEventAttendeeRequest.getComment());
      // Set as an organizer if possible
      eventAttendee.setOrganizer(addNewEventAttendeeRequest.isOrganizer());
    }
  }

  /**
   * Adds new attendees to a calendar event specified by the given AddNewEventAttendeesRequest.
   *
   * <p>This method retrieves the calendar event from Google Calendar using the provided calendar and event IDs.
   * If the event exists, the new attendees are added to the event and the event is updated on the calendar.
   * If the event does not exist or cannot be found, an info log is recorded. If an error occurs during the update,
   * an error log is recorded and an UnableToCompleteOperationException is thrown.</p>
   *
   * @param addNewEventAttendeesRequest the request object containing calendar ID, event ID, and new attendees' email addresses
   * @return a GoogleAddNewCalendarEventAttendeesResponse containing the updated event details
   * @throws UnableToCompleteOperationException if the attendees cannot be added
   */
  @MeasureExecutionTime
  @Override
  public GoogleAddNewCalendarEventAttendeesResponse addNewAttendeesToCalendarEvent(final AddNewEventAttendeesRequest addNewEventAttendeesRequest) {
    try {
      // Retrieve calendar ID and event ID from the  request
      final String calendarId = addNewEventAttendeesRequest.getCalendarId();
      final String eventId = addNewEventAttendeesRequest.getEventId();

      // Retrieve the event from the calendar
      final Event event = calendar.events()
              .get(calendarId, eventId)
              .execute();

      // If the event exists, add the attendees and update the event on the calendar
      if (nonNull(event)) {
        // Create a list of EventAttendees to be added
        final List<EventAttendee> attendees = addOrInviteAttendeesOrGuests(addNewEventAttendeesRequest.getAttendeesOrGuestsEmailAddresses());
        final List<EventAttendee> attendees2 = addOrInviteAttendeesOrGuests(addNewEventAttendeesRequest.getAttendeeOrGuests());

        // Create attendee list or register to add attendees
        initializeEventAttendeeList(event);
        // Add new attendees to already existing attendees list
        addAttendees(event, attendees);
        addAttendees(event, attendees2);
        // Save event with new attendees
        calendar.events()
          .update(calendarId, eventId, event)
          .setSendUpdates(EventSendUpdate.all())
          .execute();

        return GoogleAddNewCalendarEventAttendeesResponse.of(eventId, mapToEventExpanded(event));
      }
      logIfEnabled(log::isErrorEnabled, () -> log.error("Cannot add attendees. Event does not exist or cannot be found. {}", addNewEventAttendeesRequest.getEventId()));
    } catch (final IOException ex) {
      final String errorMessage = String.format("Error has occurred while adding attendees. Reason: %s", ex.getMessage());
      reporterService.sendMessage(errorMessage, GOOGLE_CALENDAR);
    }
    throw new UnableToCompleteOperationException();
  }

  /**
   * Handles the process of marking an attendee as not attending an event.
   *
   * @param notAttendingEventRequest the request containing information about the event and attendee.
   * @return a response object containing details of the updated event.
   * @throws UnableToCompleteOperationException if the operation cannot be completed.
   */
  @MeasureExecutionTime
  @Override
  public GoogleRetrieveCalendarEventResponse notAttendingEvent(final NotAttendingEventRequest notAttendingEventRequest) {
    try {
      // Retrieve calendar ID and event ID from the cancellation request
      final String calendarId = notAttendingEventRequest.calendarId();
      final String eventId = notAttendingEventRequest.eventId();

      // Retrieve the event from the calendar
      final RetrieveCalendarEventRequest retrieveCalendarEventRequest =  RetrieveCalendarEventRequest.of(calendarId, eventId);
      final GoogleRetrieveCalendarEventResponse retrieveCalendarEventResponse = googleCalendarEventSearchService.retrieveEvent(retrieveCalendarEventRequest);

      // If the event exists, update its visibility and patch it on the calendar
      if (nonNull(retrieveCalendarEventResponse)) {
        // Extract the retrieved calendar from the response
        final Event event = retrieveCalendarEventResponse.event();
        final String attendeeToRemove = notAttendingEventRequest.attendeeEmailAddress();
        // Iterate and find the attendee's entry to remove from the list by their email address
        final List<EventAttendee> updatedAttendees = event.getAttendees()
          .stream().filter(attendee -> !attendeeToRemove.equals(attendee.getEmail()))
          .toList();
        event.setAttendees(updatedAttendees);

        final Event patchedEvent = calendar.events()
          .patch(calendarId, eventId, event)
          .execute();

        return GoogleRetrieveCalendarEventResponse.of(notAttendingEventRequest.eventId(), mapToEventExpanded(patchedEvent), event);
      }
      logIfEnabled(log::isErrorEnabled, () -> log.error("Cannot update event. Event does not exist or cannot be found. {}", notAttendingEventRequest.eventId()));
    } catch (final IOException ex) {
      final String errorMessage = String.format("Error has occurred while update the event visibility. Reason: %s", ex.getMessage());
      reporterService.sendMessage(errorMessage, GOOGLE_CALENDAR);
    }
    throw new UnableToCompleteOperationException();
  }

  /**
   * Creates and returns a list of event attendees based on the provided email addresses.
   *
   * <p>This method converts a list of email addresses into a list of {@link EventAttendee}
   * objects for inclusion in an event invitation or attendee list.</p>
   *
   * <p>Blank or empty email addresses are filtered out before creating attendee objects.</p>
   *
   * @param attendeeOrGuests a list of details of attendees or guests including email address and alias
   * @return a list of {@link EventAttendee} objects representing the attendees or guests
   */
  public static List<EventAttendee> addOrInviteAttendeesOrGuests(final List<CreateEventDto.EventAttendeeOrGuest> attendeeOrGuests) {
    final List<EventAttendee> attendees = new ArrayList<>();
    if (nonNull(attendeeOrGuests)) {
      attendeeOrGuests.stream()
        .filter(Objects::nonNull)
        .forEach(attendeeOrGuest -> {
          final EventAttendee attendee = new EventAttendee();
          // Set attendee basic details like email and display name
          updateAttendeeBasicInfo(attendeeOrGuest, attendee);
          // Set response status to accepted if the attendee is the organizer
          determineIfAttendeeIsOrganizer(attendee);
          attendees.add(attendee);
      });
    }

    return attendees;
  }

  /**
   * Determines if the given attendee is an organizer and updates their response status accordingly.
   *
   * @param attendee the {@link EventAttendee} to check for organizer status.
   */
  private static void determineIfAttendeeIsOrganizer(final EventAttendee attendee) {
    // Check if the attendee is not null and is marked as an organizer
    if (nonNull(attendee) && nonNull(attendee.getOrganizer())) {
      // Set the response status to accepted if the attendee is an organizer
      attendee.setResponseStatus(EventAttendeeDecisionToJoin.accepted());
    }
  }

  /**
   * Updates the basic information of an attendee based on the provided attendee or guest data.
   *
   * @param attendeeOrGuest the source data containing attendee or guest information.
   * @param attendee the {@link EventAttendee} object to be updated.
   */
  private static void updateAttendeeBasicInfo(final CreateEventDto.EventAttendeeOrGuest attendeeOrGuest, final EventAttendee attendee) {
    if (nonNull(attendeeOrGuest) && nonNull(attendee)) {
      // Set the display name from the attendee or guest data
      attendee.setDisplayName(attendeeOrGuest.getAliasOrDisplayName());
      // Set the email address from the attendee or guest data
      attendee.setEmail(attendeeOrGuest.getEmailAddress());
      // Set the organizer status from the attendee or guest data
      attendee.setOrganizer(attendeeOrGuest.getIsOrganizer());
    }
  }

  /**
   * Creates a list of EventAttendee objects from a set of email addresses.
   *
   * <p>This method filters out any null email addresses from the provided set and creates an EventAttendee
   * object for each valid email address. The created EventAttendee objects are added to a list, which is then returned.</p>
   *
   * @param attendeeOrGuestEmailAddresses the set of email addresses to convert into EventAttendee objects
   * @return a list of {@link EventAttendee} objects corresponding to the provided email addresses
   */
  private List<EventAttendee> addOrInviteAttendeesOrGuests(final Set<String> attendeeOrGuestEmailAddresses) {
    final List<EventAttendee> attendees = new ArrayList<>();
    if (nonNull(attendeeOrGuestEmailAddresses)) {
      attendeeOrGuestEmailAddresses
        .stream()
        .filter(Objects::nonNull)
        .forEach(attendeeOrGuestEmailAddress -> {
          final EventAttendee attendee = new EventAttendee();
          attendee.setEmail(attendeeOrGuestEmailAddress);
          attendees.add(attendee);
      });
    }

    return attendees;
  }

  /**
   * Initializes the attendee list for the given {@link Event} if it is not already initialized.
   * This method checks if the provided {@link Event} object is non-null and whether its attendee list is null.
   * If the attendee list is null, it initializes it as an empty {@link ArrayList}.
   *
   * @param event The {@link Event} for which the attendee list is to be initialized.
   *              If the event or its attendee list is null, the attendee list will be set to an empty list.
   */
  private void initializeEventAttendeeList(final Event event) {
    if (nonNull(event) && isNull(event.getAttendees())) {
      event.setAttendees(new ArrayList<>());
    }
  }

  /**
   * Adds an attendee to the given {@link Event} if they are not already in the attendee list.
   * This method first ensures that the attendee list of the event is initialized.
   * It then checks whether an attendee with the same email address as the provided {@link EventAttendee}
   * already exists in the list. If no matching attendee is found, the new attendee is added to the list.
   *
   * @param event         The {@link Event} to which the attendee is to be added. If the event's attendee list is null,
   *                      it will be initialized as an empty list.
   * @param eventAttendee The {@link EventAttendee} to be added to the event's attendee list.
   *                      This attendee will only be added if no attendee with the same email exists in the list.
   */
  protected void addAttendee(final Event event, final EventAttendee eventAttendee) {
    // Check if there are existence or non-existence list of attendees and initialize one
    initializeEventAttendeeList(event);
    // Filter to check if the attendee already exist in the list of registered attendees
    final boolean attendeeExists = event.getAttendees().stream()
      .anyMatch(existingAttendee -> existingAttendee.getEmail().equalsIgnoreCase(eventAttendee.getEmail()));

    // If attendee does not exists in the list of attendees, add it to the list
    if (!attendeeExists) {
      event.getAttendees().add(eventAttendee);
    }
  }

  /**
   * Adds a list of attendees to the given {@link Event}, ensuring that each attendee is added
   * only if they are not already in the attendee list.
   * This method iterates through the provided list of {@link EventAttendee} objects and adds each one
   * to the event's attendee list using the {@code addAttendee} method. It first checks if the list of attendees
   * is non-null and not empty, and then filters out any null attendees before attempting to add them.
   *
   * @param event          The {@link Event} to which the attendees are to be added. The attendee list of the event
   *                       will be initialized if it is null.
   * @param eventAttendees A list of {@link EventAttendee} objects to be added to the event.
   *                       Each attendee will be added only if they are not null and do not already exist in the list.
   */
  protected void addAttendees(final Event event, final List<EventAttendee> eventAttendees) {
    if (nonNull(eventAttendees) && !eventAttendees.isEmpty()) {
      eventAttendees.stream()
        .filter(Objects::nonNull)
        .forEach(eventAttendee -> addAttendee(event, eventAttendee));
    }
  }

}
