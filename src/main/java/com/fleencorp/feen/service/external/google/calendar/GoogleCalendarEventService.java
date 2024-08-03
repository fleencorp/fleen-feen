package com.fleencorp.feen.service.external.google.calendar;

import com.fleencorp.feen.aspect.MeasureExecutionTime;
import com.fleencorp.feen.constant.external.google.calendar.ConferenceSolutionType;
import com.fleencorp.feen.constant.external.google.calendar.event.EventSendUpdate;
import com.fleencorp.feen.constant.external.google.calendar.event.EventStatus;
import com.fleencorp.feen.exception.stream.UnableToCompleteOperationException;
import com.fleencorp.feen.mapper.external.GoogleCalendarEventMapper;
import com.fleencorp.feen.model.dto.event.CreateCalendarEventDto;
import com.fleencorp.feen.model.request.calendar.event.*;
import com.fleencorp.feen.model.response.external.google.calendar.event.*;
import com.fleencorp.feen.service.report.ReporterService;
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
import java.util.*;

import static com.fleencorp.feen.constant.base.ReportMessageType.GOOGLE_CALENDAR;
import static com.fleencorp.feen.mapper.external.GoogleCalendarEventMapper.mapToEventExpanded;
import static com.fleencorp.feen.util.DateTimeUtil.toMilliseconds;
import static com.fleencorp.feen.util.external.google.GoogleApiUtil.toDateTime;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;

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
  private final ReporterService reporterService;

  /**
   * Constructs a new CalendarService with the specified Calendar instance.
   *
   * @param calendar  the Calendar instance to be used by this service
   * @param reporterService The service used for reporting events.
   */
  public GoogleCalendarEventService(
      final Calendar calendar,
      final ReporterService reporterService) {
    this.calendar = calendar;
    this.reporterService = reporterService;
  }

  /**
   * Lists events from Google Calendar based on the provided request parameters.
   *
   * <p>This method retrieves events from the specified calendar within the given time range and
   * based on other criteria provided in the request. If an error occurs during the retrieval process, it is logged.</p>
   *
   * @param listCalendarEventRequest the request object containing various parameters for listing events
   * @return {@link GoogleListCalendarEventResponse} the response containing the events result
   *
   * @see <a href="https://developers.google.com/calendar/api/v3/reference/events/list">Events: list</a>
   */
  @MeasureExecutionTime
  public GoogleListCalendarEventResponse listEvent(final ListCalendarEventRequest listCalendarEventRequest) {
    try {
      // Retrieve events from the calendar based on the request parameters
      final Events events = calendar.events()
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
    } catch (final IOException ex) {
      final String errorMessage = String.format("Error occurred while listing event. Reason: %s", ex.getMessage());
      reporterService.sendMessage(errorMessage, GOOGLE_CALENDAR);
    }
    return GoogleListCalendarEventResponse.of();
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
   * @return {@link GoogleCreateCalendarEventResponse} the response containing the created event
   * @throws UnableToCompleteOperationException if the operation cannot be completed
   *
   * @see <a href="https://developers.google.com/calendar/api/v3/reference/events/insert">Events: insert </a>
   */
  @MeasureExecutionTime
  public GoogleCreateCalendarEventResponse createEvent(final CreateCalendarEventRequest createCalendarEventRequest) {
    try {
      final Event event = new Event();

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
      final Event.Creator creatorOfEvent = new Event.Creator()
        .setEmail(createCalendarEventRequest.getCreatorEmail())
        .setDisplayName(createCalendarEventRequest.getCreatorDisplayName());
      event.setCreator(creatorOfEvent);

      // Set organizer
      // The organizer is usually the primary contact for the event and may differ from the creator,
      // especially in scenarios where someone other than the creator is leading or organizing the event.
      final Event.Organizer organizer = new Event.Organizer()
        .setEmail(createCalendarEventRequest.getOrganizerEmail())
        .setDisplayName(createCalendarEventRequest.getOrganizerDisplayName());
      event.setOrganizer(organizer);

      // Set conference details
      final ConferenceSolution conferenceSolution = new ConferenceSolution();
      conferenceSolution.setName(getDefaultConferenceSolutionName());

      final ConferenceSolutionKey conferenceSolutionKey = new ConferenceSolutionKey();
      conferenceSolutionKey.setType(ConferenceSolutionType.getDefault().getValue());

      final CreateConferenceRequest createConferenceRequest = new CreateConferenceRequest();
      createConferenceRequest.setRequestId(
          createConferenceRequestId(
          createCalendarEventRequest.getStartDateTime(),
          createCalendarEventRequest.getEndDateTime(),
          createCalendarEventRequest.getTimezone()
        ));
      createConferenceRequest.setConferenceSolutionKey(conferenceSolutionKey);

      final ConferenceData conferenceData = new ConferenceData();
      conferenceData.setCreateRequest(createConferenceRequest);

      conferenceData.setConferenceSolution(conferenceSolution);
      event.setConferenceData(conferenceData);

      // Set event start and end times
      final EventDateTime eventStartDateTime = createEventDateAndTime(createCalendarEventRequest.getStartDateTime(), createCalendarEventRequest.getTimezone());
      event.setStart(eventStartDateTime);

      final EventDateTime eventEndDateTime = createEventDateAndTime(createCalendarEventRequest.getEndDateTime(), createCalendarEventRequest.getTimezone());
      event.setEnd(eventEndDateTime);

      // Set attendees or guests
      final List<EventAttendee> attendees = addOrInviteAttendeesOrGuests(createCalendarEventRequest.getAttendeeOrGuestEmailAddresses());
      event.setAttendees(attendees);

      // Set event reminders
      final Event.Reminders reminders = new Event.Reminders()
              .setUseDefault(false)
              .setOverrides(createAndReturnEventReminders());
      event.setReminders(reminders);

      // Set extended properties (additional metadata)
      event.setExtendedProperties(new Event.ExtendedProperties().setShared(createCalendarEventRequest.getEventMetaData()));

      // Insert the event into the calendar
      final Calendar.Events.Insert insert = calendar
              .events()
              .insert(createCalendarEventRequest.getCalendarIdOrName(), event);
      insert.setConferenceDataVersion(1);
      insert.setSendUpdates(EventSendUpdate.ALL.getValue());
      final Event newEvent = insert.execute();

      if (nonNull(newEvent)) {
        return GoogleCreateCalendarEventResponse.of(event.getId(), requireNonNull(mapToEventExpanded(newEvent)));
      }
    } catch (final IOException ex) {
      final String errorMessage = String.format("Error has occurred while creating an event. Reason: %s", ex.getMessage());
      reporterService.sendMessage(errorMessage, GOOGLE_CALENDAR);
    }
    throw new UnableToCompleteOperationException();
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
   * @return {@link GoogleCancelCalendarEventResponse} the response containing the cancelled event
   * @throws UnableToCompleteOperationException if the operation cannot be completed
   */
  @MeasureExecutionTime
  public GoogleCancelCalendarEventResponse cancelEvent(final CancelCalendarEventRequest cancelCalendarEventRequest) {
    try {
      // Retrieve calendar ID and event ID from the cancellation request
      final String calendarId = cancelCalendarEventRequest.getCalendarId();
      final String eventId = cancelCalendarEventRequest.getEventId();

      // Retrieve the event from the calendar
      final Event event = calendar.events().get(calendarId, eventId).execute();

      // If the event exists, set its status to cancelled and update it on the calendar
      if (nonNull(event)) {
        event.setStatus(EventStatus.CANCELLED.getValue());
        calendar.events()
                .update(calendarId, eventId, event)
                .setSendUpdates(EventSendUpdate.ALL.getValue())
                .execute();

        return GoogleCancelCalendarEventResponse.of(cancelCalendarEventRequest.getEventId(), mapToEventExpanded(event));
      }
      log.error("Cannot cancel event. Event does not exist or cannot be found. {}", cancelCalendarEventRequest.getEventId());
    } catch (final IOException ex) {
      final String errorMessage = String.format("Error has occurred while cancelling the event. Reason: %s", ex.getMessage());
      reporterService.sendMessage(errorMessage, GOOGLE_CALENDAR);
    }
    throw new UnableToCompleteOperationException();
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
   * @return {@link GoogleRescheduleCalendarEventResponse} the response containing the rescheduled event
   * @throws UnableToCompleteOperationException if the operation cannot be completed
   */
  @MeasureExecutionTime
  public GoogleRescheduleCalendarEventResponse rescheduleEvent(final RescheduleCalendarEventRequest rescheduleCalendarEventRequest) {
    final String calendarId = rescheduleCalendarEventRequest.getCalendarId();
    final String eventId = rescheduleCalendarEventRequest.getEventId();
    try {
      // Retrieve the event from the calendar
      final Event event = calendar.events().get(calendarId, eventId).execute();

      if (nonNull(event)) {
        // Convert the new start time to DateTime and set it
        final EventDateTime eventStartDateTime = createEventDateAndTime(rescheduleCalendarEventRequest.getStartDateTime(), rescheduleCalendarEventRequest.getTimezone());
        event.setStart(eventStartDateTime);

        // Convert the new end time to DateTime and set it
        final EventDateTime eventEndDateTime = createEventDateAndTime(rescheduleCalendarEventRequest.getEndDateTime(), rescheduleCalendarEventRequest.getTimezone());
        event.setEnd(eventEndDateTime);

        // Update the event on the calendar
        calendar.events()
          .update(calendarId, eventId, event)
          .setSendUpdates(EventSendUpdate.ALL.getValue())
          .execute();

        return GoogleRescheduleCalendarEventResponse.of(rescheduleCalendarEventRequest.getEventId(), mapToEventExpanded(event));
      }
      log.error("Cannot reschedule event. Event does not exist or cannot be found. {}", rescheduleCalendarEventRequest.getEventId());
    } catch (final IOException ex) {
      final String errorMessage = String.format("Error has occurred while updating the event. Reason: %s", ex.getMessage());
      reporterService.sendMessage(errorMessage, GOOGLE_CALENDAR);
    }
    throw new UnableToCompleteOperationException();
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
   * @return {@link GoogleDeleteCalendarEventResponse} the response containing the deleted event
   *
   * @see <a href="https://developers.google.com/calendar/api/v3/reference/events/delete">Events: delete</a>
   */
  @MeasureExecutionTime
  public GoogleDeleteCalendarEventResponse deleteEvent(final DeleteCalendarEventRequest deleteCalendarEventRequest) {
    try {
      // Retrieve calendar ID and event ID from the deletion request
      final String calendarId = deleteCalendarEventRequest.getCalendarId();
      final String eventId = deleteCalendarEventRequest.getEventId();

      // Retrieve the event from the calendar
      final Event event = calendar.events().get(calendarId, eventId).execute();

      // If the event exists, delete it from the calendar and notify attendees or guests
      if (nonNull(event)) {
          calendar.events()
            .delete(calendarId, eventId)
            .setSendUpdates(EventSendUpdate.ALL.getValue())
            .execute();

        return GoogleDeleteCalendarEventResponse.of(deleteCalendarEventRequest.getEventId(), mapToEventExpanded(event));
      }
    } catch (final IOException ex) {
      final String errorMessage = String.format("Error has occurred while deleting the event. Reason: %s", ex.getMessage());
      reporterService.sendMessage(errorMessage, GOOGLE_CALENDAR);
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
   * @return {@link GoogleRetrieveCalendarEventResponse} the response containing the retrieved event
   *
   * @see <a href="https://developers.google.com/calendar/api/v3/reference/events/get">Events: get</a>
   */
  @MeasureExecutionTime
  public GoogleRetrieveCalendarEventResponse retrieveEvent(final RetrieveCalendarEventRequest retrieveCalendarEventRequest) {
    try {
      // Retrieve calendar ID and event ID from the retrieval request
      final String calendarId = retrieveCalendarEventRequest.getCalendarId();
      final String eventId = retrieveCalendarEventRequest.getEventId();

      // Retrieve the event from the calendar
      final Event event = calendar.events()
              .get(calendarId, eventId)
              .execute();

      if (nonNull(event)) {
        return GoogleRetrieveCalendarEventResponse.of(eventId, event, mapToEventExpanded(event));
      }

      log.error("Cannot retrieve event. Event does not exist or cannot be found. {}", eventId);
    } catch (final IOException ex) {
      final String errorMessage = String.format("Error has occurred while retrieving the event. Reason: %s", ex.getMessage());
      reporterService.sendMessage(errorMessage, GOOGLE_CALENDAR);
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
   * @return {@link GoogleAddNewCalendarEventAttendeeResponse} the response containing the attendee that was added to the event
   * @throws UnableToCompleteOperationException if the operation cannot be completed
   */
  @MeasureExecutionTime
  public GoogleAddNewCalendarEventAttendeeResponse addNewAttendeeToCalendarEvent(final AddNewEventAttendeeRequest addNewEventAttendeeRequest) {
    try {
      // Retrieve calendar ID and event ID from the  request
      final String calendarId = addNewEventAttendeeRequest.getCalendarId();
      final String eventId = addNewEventAttendeeRequest.getEventId();

      // Retrieve the event from the calendar
      final Event event = calendar.events().get(calendarId, eventId).execute();

      // If the event exists, add the new attendee and update the event on the calendar
      if (nonNull(event)) {
        final EventAttendee eventAttendee = new EventAttendee();
        eventAttendee.setEmail(addNewEventAttendeeRequest.getAttendeeEmailAddress());
        eventAttendee.setDisplayName(addNewEventAttendeeRequest.getAttendeeAliasOrDisplayName());
        event.getAttendees().add(eventAttendee);

        calendar.events()
          .update(calendarId, eventId, event)
          .execute();

        return GoogleAddNewCalendarEventAttendeeResponse
          .of(addNewEventAttendeeRequest.getEventId(),
              addNewEventAttendeeRequest.getAttendeeEmailAddress(),
              mapToEventExpanded(event));
      }
      log.error("Cannot add new attendee. Event does not exist or cannot be found. {}", addNewEventAttendeeRequest.getEventId());
    } catch (final IOException ex) {
      final String errorMessage = String.format("Error has occurred while adding an attendee. Reason: %s", ex.getMessage());
      reporterService.sendMessage(errorMessage, GOOGLE_CALENDAR);
    }
    throw new UnableToCompleteOperationException();
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
        // Add new attendees to already existing attendees list
        event.getAttendees().addAll(attendees);
        // Save event with new attendees
        calendar.events()
          .update(calendarId, eventId, event)
          .setSendUpdates(EventSendUpdate.ALL.getValue())
          .execute();

        return GoogleAddNewCalendarEventAttendeesResponse.of(eventId, mapToEventExpanded(event));
      }
      log.error("Cannot add attendees. Event does not exist or cannot be found. {}", addNewEventAttendeesRequest.getEventId());
    } catch (final IOException ex) {
      final String errorMessage = String.format("Error has occurred while adding attendees. Reason: %s", ex.getMessage());
      reporterService.sendMessage(errorMessage, GOOGLE_CALENDAR);
    }
    throw new UnableToCompleteOperationException();
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
   * @return {@link GooglePatchCalendarEventResponse} the response containing the patched event
   * @throws UnableToCompleteOperationException if the operation cannot be completed
   */
  @MeasureExecutionTime
  public GooglePatchCalendarEventResponse patchEvent(final PatchCalendarEventRequest patchCalendarEventRequest) {
    try {
      // Retrieve calendar ID and event ID from the cancellation request
      final String calendarId = patchCalendarEventRequest.getCalendarId();
      final String eventId = patchCalendarEventRequest.getEventId();

      // Create a request to retrieve an event from a calendar
      final RetrieveCalendarEventRequest retrieveCalendarEventRequest = RetrieveCalendarEventRequest.of(calendarId, eventId);
      // Retrieve the event from the calendar
      final GoogleRetrieveCalendarEventResponse googleRetrieveCalendarEventResponse = retrieveEvent(retrieveCalendarEventRequest);

      // If the event exists, update its title and description and patch it on the calendar
      if (nonNull(googleRetrieveCalendarEventResponse)) {
        // Retrieve calendar event from the response and set new details
        final Event event = googleRetrieveCalendarEventResponse.getCalendarEvent();
        event.setSummary(patchCalendarEventRequest.getTitle());
        event.setDescription(patchCalendarEventRequest.getDescription());

        // Save event with updated summary, description and other details
        final Event patchedEvent = calendar.events()
                .patch(calendarId, eventId, event)
                .setSendUpdates(EventSendUpdate.ALL.getValue())
                .execute();

        return GooglePatchCalendarEventResponse.of(patchCalendarEventRequest.getEventId(), mapToEventExpanded(patchedEvent));
      }
      log.error("Cannot patch or update event. Event does not exist or cannot be found. {}", patchCalendarEventRequest.getEventId());
    } catch (final IOException ex) {
      final String errorMessage = String.format("Error has occurred while patching the event. Reason: %s", ex.getMessage());
      reporterService.sendMessage(errorMessage, GOOGLE_CALENDAR);
    }
    throw new UnableToCompleteOperationException();
  }

  /**
   * Creates an instant event on Google Calendar based on the provided request.
   *
   * <p>This method uses the quickAdd feature of the Google Calendar API to create an event
   * instantly with a simple text string. If an error occurs during the creation process, it is logged.</p>
   *
   * @param createInstantCalendarEventRequest the request object containing the calendar ID and event title
   * @return {@link GoogleCreateInstantCalendarEventResponse} the response containing the instant event that was created
   * @throws UnableToCompleteOperationException if the operation cannot be completed
   */
  @MeasureExecutionTime
  public GoogleCreateInstantCalendarEventResponse createInstantEvent(final CreateInstantCalendarEventRequest createInstantCalendarEventRequest) {
    try {
      // Use the quickAdd feature to create an instant event with the provided title
      final Event event = calendar.events()
              .quickAdd(createInstantCalendarEventRequest.getCalendarId(),
                        createInstantCalendarEventRequest.getTitle())
              .setSendNotifications(createInstantCalendarEventRequest.getSendNotifications())
              .setSendUpdates(EventSendUpdate.ALL.getValue())
              .execute();

      return GoogleCreateInstantCalendarEventResponse.of(event.getId(), mapToEventExpanded(event));
    } catch (final IOException ex) {
      final String errorMessage = String.format("Error has occurred while creating an instant event. Reason: %s", ex.getMessage());
      reporterService.sendMessage(errorMessage, GOOGLE_CALENDAR);
    }
    throw new UnableToCompleteOperationException();
  }

  /**
   * Updates the visibility of a calendar event specified by the given UpdateCalendarEventVisibilityRequest.
   *
   * <p>This method retrieves the calendar event from Google Calendar using the provided calendar and event IDs.
   * If the event exists, its visibility is updated and patched on the calendar. If the event does not exist
   * or cannot be found, an info log is recorded. If an error occurs during the update, an error log is recorded
   * and an UnableToCompleteOperationException is thrown.</p>
   *
   * @param updateCalendarEventVisibilityRequest the request object containing calendar ID, event ID, and new visibility
   * @return {@link GooglePatchCalendarEventResponse} containing the updated event details
   * @throws UnableToCompleteOperationException if the event cannot be updated
   */
  @MeasureExecutionTime
  public GooglePatchCalendarEventResponse updateEventVisibility(final UpdateCalendarEventVisibilityRequest updateCalendarEventVisibilityRequest) {
    try {
      // Retrieve calendar ID and event ID from the cancellation request
      final String calendarId = updateCalendarEventVisibilityRequest.getCalendarId();
      final String eventId = updateCalendarEventVisibilityRequest.getEventId();

      // Retrieve the event from the calendar
      final Event event = calendar
              .events()
              .get(calendarId, eventId)
              .execute();

      // If the event exists, update its visibility and patch it on the calendar
      if (nonNull(event)) {
        event.setVisibility(updateCalendarEventVisibilityRequest.getVisibility().getValue());
        final Event patchedEvent = calendar.events()
                .patch(calendarId, eventId, event)
                .execute();

        return GooglePatchCalendarEventResponse.of(updateCalendarEventVisibilityRequest.getEventId(), mapToEventExpanded(patchedEvent));
      }
      log.error("Cannot update visibility. Event does not exist or cannot be found. {}", updateCalendarEventVisibilityRequest.getEventId());
    } catch (final IOException ex) {
      final String errorMessage = String.format("Error has occurred while update the event visibility. Reason: %s", ex.getMessage());
      reporterService.sendMessage(errorMessage, GOOGLE_CALENDAR);
    }
    throw new UnableToCompleteOperationException();
  }

  /**
   * Retrieves the default conference solution name.
   *
   * <p>This method returns the default name of the conference solution used for events.</p>
   *
   * @return the default conference solution name
   */
  public String getDefaultConferenceSolutionName() {
    return GoogleCalendarEventService.DEFAULT_CONFERENCE_SOLUTION_NAME;
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
  public static LocalDateTime toLocalDateTime(final DateTime dateTime) {
    if (nonNull(dateTime)) {
      final Date date = new Date(dateTime.getValue());
      final Instant instant = date.toInstant();
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
  private String createConferenceRequestId(final LocalDateTime startDate, final LocalDateTime endDate, final String timezone) {
    return toMilliseconds(startDate, timezone) + "-" + toMilliseconds(endDate, timezone);
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
  private List<EventAttendee> addOrInviteAttendeesOrGuests(final List<CreateCalendarEventDto.EventAttendeeOrGuest> attendeeOrGuests) {
    final List<EventAttendee> attendees = new ArrayList<>();
    if (nonNull(attendeeOrGuests)) {
      attendeeOrGuests
        .stream()
        .filter(Objects::nonNull)
        .forEach(attendeeOrGuest -> {
          final EventAttendee attendee = new EventAttendee();
          attendee.setDisplayName(attendeeOrGuest.getAliasOrDisplayName());
          attendee.setEmail(attendeeOrGuest.getEmailAddress());
          attendee.setOrganizer(attendeeOrGuest.getIsOrganizer());
          if (nonNull(attendee.getOrganizer()) && attendee.getOrganizer()) {
            attendee.setResponseStatus("accepted");
          }
          attendees.add(attendee);
      });
    }

    return attendees;
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
    final List<EventReminder> eventReminders = new ArrayList<>();
    final int aDayOrOneDayInHours = 24 * 60;
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
   * @param localDateTime the LocalDateTime object representing the date and time of the event
   * @param timezone the timezone to associate with the event date and time
   * @return an EventDateTime object initialized with the provided LocalDateTime and timezone
   */
  private EventDateTime createEventDateAndTime(final LocalDateTime localDateTime, final String timezone) {
    final DateTime dateTime = toDateTime(localDateTime);
    return createEventDateAndTime(dateTime, timezone);
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
  private EventDateTime createEventDateAndTime(final DateTime dateTime, final String timezone) {
    final EventDateTime eventDateTime = new EventDateTime();
    eventDateTime.setDateTime(dateTime);
    eventDateTime.setTimeZone(timezone);

    return eventDateTime;
  }

}
