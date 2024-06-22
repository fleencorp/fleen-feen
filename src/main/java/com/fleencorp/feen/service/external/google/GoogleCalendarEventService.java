package com.fleencorp.feen.service.external.google;

import com.fleencorp.feen.constant.external.google.calendar.ConferenceSolutionType;
import com.fleencorp.feen.constant.external.google.calendar.event.EventSendUpdate;
import com.fleencorp.feen.constant.external.google.calendar.event.EventStatus;
import com.fleencorp.feen.mapper.GoogleCalendarEventMapper;
import com.fleencorp.feen.model.request.calendar.event.*;
import com.fleencorp.feen.model.response.google.calendar.event.*;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static com.fleencorp.feen.util.DateTimeUtil.toMilliseconds;
import static com.fleencorp.feen.util.external.google.GoogleApiUtil.toDateTime;
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
public class GoogleCalendarEventService {

  @Getter
  private static final String DEFAULT_CONFERENCE_SOLUTION_NAME = "Google Meet";

  private final Calendar calendar;

  /**
   * Constructs a new CalendarService with the specified Calendar instance.
   *
   * @param calendar  the Calendar instance to be used by this service
   */
  public GoogleCalendarEventService(Calendar calendar) {
    this.calendar = calendar;
  }

  /**
   * Lists events from Google Calendar based on the provided request parameters.
   *
   * <p>This method retrieves events from the specified calendar within the given time range and
   * based on other criteria provided in the request. If an error occurs during the retrieval process, it is logged.</p>
   *
   * @param listCalendarEventRequest the request object containing various parameters for listing events
   * @return {@link ListCalendarEventResponse} the response containing the events result
   *
   * @see <a href="https://developers.google.com/calendar/api/v3/reference/events/list">Events: list</a>
   */
  public ListCalendarEventResponse listEvent(ListCalendarEventRequest listCalendarEventRequest) {
    try {
      // Retrieve events from the calendar based on the request parameters
      Events events = calendar.events()
              .list(listCalendarEventRequest.getCalendarId())
              .setMaxResults(listCalendarEventRequest.getMaxResultOrLimit())
              .setTimeMin(toDateTime(listCalendarEventRequest.getFrom()))
              .setTimeMax(toDateTime(listCalendarEventRequest.getTo()))
              .setSingleEvents(listCalendarEventRequest.getSingleEvents())
              .setShowDeleted(listCalendarEventRequest.getShowDeleted())
              .setOrderBy(listCalendarEventRequest.getOrderBy().getValue())
              .setQ(listCalendarEventRequest.getQ())
              .setPageToken(listCalendarEventRequest.getPageToken())
              .setTimeZone(listCalendarEventRequest.getTimezone())
              .execute();

      return GoogleCalendarEventMapper.mapToCalendarEventResponse(events);
    } catch (IOException ex) {
      log.error("Error occurred while listing event. Reason: {}", ex.getMessage());
    }
    return ListCalendarEventResponse.builder()
            .build();
  }

  /**
   * Creates a new event on Google Calendar based on the provided request parameters.
   *
   * <p>This method constructs an Event object with details such as title, description,
   * visibility settings, organizer information, conference details, start and end times,
   * attendees, reminders, and additional metadata. The event is then inserted into the
   * specified calendar.</p>
   *
   * <p>If successful, the event is created on the calendar. If an error occurs during
   * the creation process, an IOException is caught and logged.</p>
   *
   * @param createCalendarEventRequest the request object containing parameters to create the event
   * @return {@link CreateCalendarEventResponse} the response containing the created event
   *
   * @see <a href="https://developers.google.com/calendar/api/v3/reference/events/insert">Events: insert </a>
   */
  public CreateCalendarEventResponse createEvent(CreateCalendarEventRequest createCalendarEventRequest) {
    try {
      Event event = new Event();

      // Set event details from the request object
      event.setSummary(createCalendarEventRequest.getTitle());
      event.setDescription(createCalendarEventRequest.getDescription());
      event.setVisibility(createCalendarEventRequest.getVisibility().getValue());
      event.setGuestsCanSeeOtherGuests(createCalendarEventRequest.getCanGuestsCanSeeOtherGuests());
      event.setGuestsCanInviteOthers(createCalendarEventRequest.getCanGuestsInviteOtherGuests());
      event.setLocation(createCalendarEventRequest.getLocation());

      // Set creator/organizer information
      // The creator is typically the user who directly interacts with your application's interface to create the event.
      // They may or may not be the same as the organizer.
      Event.Creator creatorOfEvent = new Event.Creator();
      creatorOfEvent.setEmail(createCalendarEventRequest.getCreatorEmail());
      creatorOfEvent.setDisplayName(createCalendarEventRequest.getCreatorDisplayName());
      event.setCreator(creatorOfEvent);

      // Set organizer
      // The organizer is usually the primary contact for the event and may differ from the creator,
      // especially in scenarios where someone other than the creator is leading or organizing the event.
      Event.Organizer organizer = new Event.Organizer()
              .setEmail(createCalendarEventRequest.getOrganizerEmail())
              .setDisplayName(createCalendarEventRequest.getOrganizerDisplayName());
      event.setOrganizer(organizer);

      // Set conference details
      ConferenceSolution conferenceSolution = new ConferenceSolution();
      conferenceSolution.setName(getDefaultConferenceSolutionName());

      ConferenceSolutionKey conferenceSolutionKey = new ConferenceSolutionKey();
      conferenceSolutionKey.setType(ConferenceSolutionType.getDefault().getValue());

      CreateConferenceRequest createConferenceRequest = new CreateConferenceRequest();
      createConferenceRequest.setRequestId(
        createConferenceRequestId(
          createCalendarEventRequest.getStartDateTime(),
          createCalendarEventRequest.getEndDateTime(),
          createCalendarEventRequest.getTimezone()
        ));
      createConferenceRequest.setConferenceSolutionKey(conferenceSolutionKey);

      ConferenceData conferenceData = new ConferenceData();
      conferenceData.setCreateRequest(createConferenceRequest);

      conferenceData.setConferenceSolution(conferenceSolution);
      event.setConferenceData(conferenceData);

      // Set event start and end times
      EventDateTime eventStartDateTime = createEventDateAndTime(createCalendarEventRequest.getStartDateTime(), createCalendarEventRequest.getTimezone());
      event.setStart(eventStartDateTime);

      EventDateTime eventEndDateTime = createEventDateAndTime(createCalendarEventRequest.getEndDateTime(), createCalendarEventRequest.getTimezone());
      event.setEnd(eventEndDateTime);

      // Set attendees or guests
      List<EventAttendee> attendees = addAttendeesOrInviteGuests(createCalendarEventRequest.getAttendeeOrGuestEmailAddresses());
      event.setAttendees(attendees);

      // Set event reminders
      Event.Reminders reminders = new Event.Reminders()
              .setUseDefault(false)
              .setOverrides(createAndReturnEventReminders());
      event.setReminders(reminders);

      // Set extended properties (additional metadata)
      event.setExtendedProperties(new Event.ExtendedProperties().setShared(createCalendarEventRequest.getEventMetaData()));

      // Insert the event into the calendar
      Calendar.Events.Insert insert = calendar
              .events()
              .insert(createCalendarEventRequest.getCalendarIdOrName(), event);
      insert.setConferenceDataVersion(1);
      insert.setSendUpdates(EventSendUpdate.ALL.getValue());
      Event newEvent = insert.execute();

      return CreateCalendarEventResponse.builder()
        .eventId(event.getId())
        .event(GoogleCalendarEventMapper.mapToEventExpanded(newEvent))
        .build();
    } catch (IOException ex) {
      log.error("Error has occurred while creating an event. Reason: {}", ex.getMessage());
    }
    return null;
  }

  /**
   * Cancels an event on Google Calendar based on the provided cancellation request.
   *
   * <p>This method retrieves the specified event from the calendar using its ID,
   * updates its status to cancelled, and then updates the event on the calendar.</p>
   *
   * <p>If successful, the event status is updated to cancelled and updates are sent
   * to all attendees. If an error occurs during the cancellation process, it is logged.</p>
   *
   * @param cancelCalendarEventRequest the request object containing the calendar ID and event ID to cancel
   * @return {@link CancelCalendarEventResponse} the response containing the cancelled event
   */
  public CancelCalendarEventResponse cancelEvent(CancelCalendarEventRequest cancelCalendarEventRequest) {
    try {
      // Retrieve calendar ID and event ID from the cancellation request
      String calendarId = cancelCalendarEventRequest.getCalendarId();
      String eventId = cancelCalendarEventRequest.getEventId();

      // Retrieve the event from the calendar
      Event event = calendar.events().get(calendarId, eventId).execute();

      // If the event exists, set its status to cancelled and update it on the calendar
      if (nonNull(event)) {
        event.setStatus(EventStatus.CANCELLED.getValue());
        calendar.events()
                .update(calendarId, eventId, event)
                .setSendUpdates(EventSendUpdate.ALL.getValue())
                .execute();

        return CancelCalendarEventResponse.builder()
                .eventId(cancelCalendarEventRequest.getEventId())
                .event(GoogleCalendarEventMapper.mapToEventExpanded(event))
                .build();
      }
    } catch (IOException ex) {
      log.error("Error has occurred while cancelling the event. Reason: {}", ex.getMessage());
    }
    return null;
  }

  /**
   * Reschedules an event on Google Calendar based on the provided reschedule request.
   *
   * <p>This method retrieves the specified event from the calendar using its ID,
   * updates its start and end times, and then updates the event on the calendar.</p>
   *
   * <p>If successful, the event times are updated and updates are sent to all attendees.
   * If an error occurs during the rescheduling process, it is logged.</p>
   *
   * @param rescheduleCalendarEventRequest the request object containing the calendar ID, event ID, new start time, and timezone
   * @return {@link RescheduleCalendarEventResponse} the response containing the rescheduled event
   */
  public RescheduleCalendarEventResponse rescheduleEvent(RescheduleCalendarEventRequest rescheduleCalendarEventRequest) {
    String calendarId = rescheduleCalendarEventRequest.getCalendarId();
    String eventId = rescheduleCalendarEventRequest.getEventId();
    try {
      // Retrieve the event from the calendar
      Event event = calendar.events().get(calendarId, eventId).execute();

      if (nonNull(event)) {
        // Convert the new start time to DateTime and set it
        EventDateTime eventStartDateTime = createEventDateAndTime(rescheduleCalendarEventRequest.getStartDateTime(), rescheduleCalendarEventRequest.getTimezone());
        event.setStart(eventStartDateTime);

        // Convert the new end time to DateTime and set it
        EventDateTime eventEndDateTime = createEventDateAndTime(rescheduleCalendarEventRequest.getEndDateTime(), rescheduleCalendarEventRequest.getTimezone());
        event.setEnd(eventEndDateTime);

        // Update the event on the calendar
        calendar.events().update(calendarId, eventId, event)
                .setSendUpdates(EventSendUpdate.ALL.getValue())
                .execute();

        return RescheduleCalendarEventResponse.builder()
                .eventId(rescheduleCalendarEventRequest.getEventId())
                .event(GoogleCalendarEventMapper.mapToEventExpanded(event))
                .build();
      }
    } catch (IOException ex) {
      log.error("Error has occurred while updating the event. Reason: {}", ex.getMessage());
    }
    return null;
  }

  /**
   * Deletes an event from Google Calendar based on the provided deletion request.
   *
   * <p>This method retrieves the specified event from the calendar using its ID and
   * deletes it if it exists. Updates are sent to all attendees about the event deletion.</p>
   *
   * <p>If an error occurs during the deletion process, it is logged.</p>
   *
   * @param deleteCalendarEventRequest the request object containing the calendar ID and event ID to delete
   * @return {@link DeleteCalendarEventResponse} the response containing the deleted event
   *
   * @see <a href="https://developers.google.com/calendar/api/v3/reference/events/delete">Events: delete</a>
   */
  public DeleteCalendarEventResponse deleteEvent(DeleteCalendarEventRequest deleteCalendarEventRequest) {
    try {
      // Retrieve calendar ID and event ID from the deletion request
      String calendarId = deleteCalendarEventRequest.getCalendarId();
      String eventId = deleteCalendarEventRequest.getEventId();

      // Retrieve the event from the calendar
      Event event = calendar.events().get(calendarId, eventId).execute();

      // If the event exists, delete it from the calendar and notify attendees or guests
      if (nonNull(event)) {
        calendar.events()
                .delete(calendarId, eventId)
                .setSendUpdates(EventSendUpdate.ALL.getValue())
                .execute();

        return DeleteCalendarEventResponse.builder()
                .eventId(deleteCalendarEventRequest.getEventId())
                .event(GoogleCalendarEventMapper.mapToEventExpanded(event))
                .build();
      }
    } catch (IOException ex) {
      log.error("Error has occurred while deleting the event. Reason: {}", ex.getMessage());
    }
    return null;
  }

  /**
   * Retrieves an event from Google Calendar based on the provided retrieval request.
   *
   * <p>This method retrieves the specified event from the calendar using its ID.
   * If an error occurs during the retrieval process, it is logged.</p>
   *
   * @param retrieveCalendarEventRequest the request object containing the calendar ID and event ID to retrieve
   * @return {@link RetrieveCalendarEventResponse} the response containing the retrieved event
   *
   * @see <a href="https://developers.google.com/calendar/api/v3/reference/events/get">Events: get</a>
   */
  public RetrieveCalendarEventResponse retrieveEvent(RetrieveCalendarEventRequest retrieveCalendarEventRequest) {
    try {
      // Retrieve calendar ID and event ID from the retrieval request
      String calendarId = retrieveCalendarEventRequest.getCalendarId();
      String eventId = retrieveCalendarEventRequest.getEventId();

      // Retrieve the event from the calendar
      Event event = calendar.events()
              .get(calendarId, eventId)
              .execute();

      if (nonNull(event)) {
        return RetrieveCalendarEventResponse.builder()
                .event(GoogleCalendarEventMapper.mapToEventExpanded(event))
                .build();
      }
    } catch (IOException ex) {
      log.error("Error has occurred while retrieving the event. Reason: {}", ex.getMessage());
    }
    return null;
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
   * @return {@link AddNewCalendarEventAttendeeResponse} the response containing the attendee that was added to the event
   */
  public AddNewCalendarEventAttendeeResponse addNewAttendeeToCalendarEvent(AddNewEventAttendeeRequest addNewEventAttendeeRequest) {
    try {
      // Retrieve calendar ID and event ID from the  request
      String calendarId = addNewEventAttendeeRequest.getCalendarId();
      String eventId = addNewEventAttendeeRequest.getEventId();

      // Retrieve the event from the calendar
      Event event = calendar.events()
              .get(calendarId, eventId)
              .execute();

      // If the event exists, add the new attendee and update the event on the calendar
      if (nonNull(event)) {
        event.setStatus(EventStatus.CANCELLED.getValue());
        event.getAttendees().add(new EventAttendee().setEmail(addNewEventAttendeeRequest.getAttendeeEmailAddress()));
        calendar.events()
                .update(calendarId, eventId, event)
                .setSendUpdates(EventSendUpdate.ALL.getValue())
                .execute();

        return AddNewCalendarEventAttendeeResponse.builder()
                .eventId(addNewEventAttendeeRequest.getEventId())
                .userEmailAddress(addNewEventAttendeeRequest.getAttendeeEmailAddress())
                .event(GoogleCalendarEventMapper.mapToEventExpanded(event))
                .build();
      }
    } catch (IOException ex) {
      log.error("Error has occurred while adding an attendee. Reason: {}", ex.getMessage());
    }
    return null;
  }

  /**
   * Patches an existing event on Google Calendar based on the provided request.
   *
   * <p>This method retrieves the specified event from the calendar using its ID,
   * updates the event's summary and description, and patches the event on the calendar.</p>
   *
   * <p>If an error occurs during the patching process, it is logged.</p>
   *
   * @param patchCalendarEventRequest the request object containing the calendar ID, event ID, new title, and description
   * @return {@link PatchCalendarEventResponse} the response containing the patched event
   */
  public PatchCalendarEventResponse patchEvent(PatchCalendarEventRequest patchCalendarEventRequest) {
    try {
      // Retrieve calendar ID and event ID from the cancellation request
      String calendarId = patchCalendarEventRequest.getCalendarId();
      String eventId = patchCalendarEventRequest.getEventId();

      // Retrieve the event from the calendar
      Event event = calendar.events().get(calendarId, eventId).execute();

      // If the event exists, update its title and description and patch it on the calendar
      if (nonNull(event)) {
        event.setSummary(patchCalendarEventRequest.getTitle());
        event.setDescription(patchCalendarEventRequest.getDescription());
        Event patchedEvent = calendar.events()
                .patch(calendarId, eventId, event)
                .setSendUpdates(EventSendUpdate.ALL.getValue())
                .execute();

        return PatchCalendarEventResponse.builder()
                .eventId(patchCalendarEventRequest.getEventId())
                .event(GoogleCalendarEventMapper.mapToEventExpanded(patchedEvent))
                .build();
      }
    } catch (IOException ex) {
      log.error("Error has occurred while patching the event. Reason: {}", ex.getMessage());
    }
    return null;
  }

  /**
   * Creates an instant event on Google Calendar based on the provided request.
   *
   * <p>This method uses the quickAdd feature of the Google Calendar API to create an event
   * instantly with a simple text string. If an error occurs during the creation process, it is logged.</p>
   *
   * @param createInstantCalendarEventRequest the request object containing the calendar ID and event title
   * @return {@link CreateInstantCalendarEventResponse} the response containing the instant event that was created
   */
  public CreateInstantCalendarEventResponse createInstantEvent(CreateInstantCalendarEventRequest createInstantCalendarEventRequest) {
    try {
      // Use the quickAdd feature to create an instant event with the provided title

      Event event = calendar.events()
              .quickAdd(createInstantCalendarEventRequest.getCalendarId(),
                        createInstantCalendarEventRequest.getTitle())
              .setSendNotifications(createInstantCalendarEventRequest.getSendNotifications())
              .setSendUpdates(EventSendUpdate.ALL.getValue())
              .execute();

      log.info(event.toPrettyString());

      return CreateInstantCalendarEventResponse.builder()
              .eventId(event.getId())
              .event(GoogleCalendarEventMapper.mapToEventExpanded(event))
              .build();
    } catch (IOException ex) {
      log.error("Error has occurred while creating an instant event. Reason: {}", ex.getMessage());
    }
    return null;
  }

  /**
   * Retrieves the default conference solution name.
   *
   * <p>This method returns the default name of the conference solution used for events.</p>
   *
   * @return the default conference solution name
   */
  public String getDefaultConferenceSolutionName() {
    return DEFAULT_CONFERENCE_SOLUTION_NAME;
  }


  /**
   * Converts a {@link DateTime} object to a {@link LocalDateTime} object.
   *
   * <p>This method converts the given {@link DateTime} to a {@link LocalDateTime}
   * using the system's default time zone. If the input is {@code null}, it returns {@code null}.</p>
   *
   * @param dateTime the {@link DateTime} object to be converted
   * @return the corresponding {@link LocalDateTime} object, or {@code null} if the input is {@code null}
   */
  public static LocalDateTime toLocalDateTime(DateTime dateTime) {
    if (nonNull(dateTime)) {
      Date date = new Date(dateTime.getValue());
      Instant instant = date.toInstant();
      return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }
    return null;
  }

  /**
   * Creates a unique conference request ID based on the start and end dates and the timezone.
   *
   * <p>This method generates a unique identifier by combining the milliseconds since epoch
   * of the start and end dates, adjusted for the specified timezone.</p>
   *
   * @param startDate the start date and time of the conference
   * @param endDate the end date and time of the conference
   * @param timezone the timezone in which the conference takes place
   * @return a unique conference request ID generated from the start and end dates
   */
  private String createConferenceRequestId(LocalDateTime startDate, LocalDateTime endDate, String timezone) {
    return String.valueOf(toMilliseconds(startDate, timezone))
            .concat("-")
            .concat(String.valueOf(toMilliseconds(endDate, timezone)));
  }

  /**
   * Creates and returns a list of event attendees based on the provided email addresses.
   *
   * <p>This method converts a list of email addresses into a list of {@link EventAttendee}
   * objects for inclusion in an event invitation or attendee list.</p>
   *
   * <p>Blank or empty email addresses are filtered out before creating attendee objects.</p>
   *
   * @param attendeeOrGuestEmailAddresses a list of email addresses of attendees or guests
   * @return a list of EventAttendee objects representing the attendees or guests
   */
  private List<EventAttendee> addAttendeesOrInviteGuests(List<String> attendeeOrGuestEmailAddresses) {
    List<EventAttendee> attendees = new ArrayList<>();
    attendeeOrGuestEmailAddresses
            .stream()
            .filter(Objects::nonNull)
            .map(String::trim)
            .filter(String::isBlank)
            .forEach(emailAddress -> {
              EventAttendee attendee = new EventAttendee();
              attendee.setEmail(emailAddress);
              attendees.add(attendee);
            });

    return attendees;
  }

  /**
   * Creates and returns a list of event reminders.
   *
   * <p>This method initializes and populates a list of {@link EventReminder} objects
   * with predefined reminder settings.</p>
   *
   * <p>The reminders set by default are:</p>
   * <ul>
   *  <li>10 minutes before the event, using a popup notification</li>
   *  <li>1 day (24 hours) before the event, using an email notification</li>
   *  <li>1 day (24 hours) before the event, using a popup notification</li>
   * </ul>
   *
   * @return a list of EventReminder objects containing the predefined reminders
   */
  private List<EventReminder> createAndReturnEventReminders() {
    List<EventReminder> eventReminders = new ArrayList<>();
    int aDayOrOneDayInHours = 24 * 60;
    eventReminders.add(new EventReminder().setMethod("popup").setMinutes(10));
    eventReminders.add(new EventReminder().setMethod("email").setMinutes(aDayOrOneDayInHours));
    eventReminders.add(new EventReminder().setMethod("popup").setMinutes(aDayOrOneDayInHours));
    return eventReminders;
  }

  /**
   * Creates an EventDateTime object from a LocalDateTime and timezone.
   *
   * <p>This method converts a LocalDateTime object to a DateTime object and then
   * creates an EventDateTime object using the specified timezone.</p>
   *
   * @param dateTime the LocalDateTime object representing the date and time of the event
   * @param timezone the timezone to associate with the event date and time
   * @return an EventDateTime object initialized with the provided LocalDateTime and timezone
   */
  private EventDateTime createEventDateAndTime(LocalDateTime dateTime, String timezone) {
    DateTime startDateTime = toDateTime(dateTime);
    return createEventDateAndTime(startDateTime, timezone);
  }

  /**
   * Creates an EventDateTime object with the specified DateTime and timezone.
   *
   * <p>This method constructs an EventDateTime object for use in Google Calendar API,
   * setting the date and time using the provided DateTime object and specifying
   * the timezone.</p>
   *
   * @param dateTime the DateTime object representing the date and time of the event
   * @param timezone the timezone to associate with the event date and time
   * @return an EventDateTime object initialized with the provided DateTime and timezone
   */
  private EventDateTime createEventDateAndTime(DateTime dateTime, String timezone) {
    EventDateTime eventDateTime = new EventDateTime();
    eventDateTime.setDateTime(dateTime);
    eventDateTime.setTimeZone(timezone);

    return eventDateTime;
  }

}
