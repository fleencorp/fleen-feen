package com.fleencorp.feen.service.impl.external.google.calendar.event;

import com.fleencorp.feen.calendar.model.request.event.create.CreateCalendarEventRequest;
import com.fleencorp.feen.calendar.model.request.event.create.CreateInstantCalendarEventRequest;
import com.fleencorp.feen.calendar.model.request.event.read.RetrieveCalendarEventRequest;
import com.fleencorp.feen.calendar.model.request.event.update.*;
import com.fleencorp.feen.common.aspect.MeasureExecutionTime;
import com.fleencorp.feen.common.constant.external.google.calendar.event.EventSendUpdate;
import com.fleencorp.feen.common.constant.external.google.calendar.event.EventStatus;
import com.fleencorp.feen.common.exception.UnableToCompleteOperationException;
import com.fleencorp.feen.common.service.report.ReporterService;
import com.fleencorp.feen.model.response.external.google.calendar.event.*;
import com.fleencorp.feen.service.external.google.calendar.event.GoogleCalendarEventSearchService;
import com.fleencorp.feen.service.external.google.calendar.event.GoogleCalendarEventService;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import static com.fleencorp.feen.common.constant.base.ReportMessageType.GOOGLE_CALENDAR;
import static com.fleencorp.feen.common.util.LoggingUtil.logIfEnabled;
import static com.fleencorp.feen.service.impl.external.google.calendar.attendee.GoogleCalendarAttendeeServiceImpl.addOrInviteAttendeesOrGuests;
import static com.fleencorp.feen.stream.mapper.external.GoogleCalendarEventMapper.mapToEventExpanded;
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
public class GoogleCalendarEventServiceImpl implements GoogleCalendarEventService {

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
  public GoogleCalendarEventServiceImpl(
      final Calendar calendar,
      final GoogleCalendarEventSearchService googleCalendarEventSearchService,
      final ReporterService reporterService) {
    this.calendar = calendar;
    this.googleCalendarEventSearchService = googleCalendarEventSearchService;
    this.reporterService = reporterService;
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
  @Override
  public GoogleCreateCalendarEventResponse createEvent(final CreateCalendarEventRequest createCalendarEventRequest) {
    try {
      final Event event = new Event();
      // Update event with basic information such as title, description, and visibility
      updateBasicEventInfo(createCalendarEventRequest, event);
      // Set the creator and organizer details for the event
      updateEventCreatorAndOrganizerDetails(createCalendarEventRequest, event);
      // Add conference details, such as Google Meet, to the event
      updateEventConferenceDetails(createCalendarEventRequest, event);
      // Schedule the event by setting its start and end times, and invite attendees or guests
      updateEventSchedulesAndAttendeesOrGuests(createCalendarEventRequest, event);
      // Add event reminders and other custom properties (metadata)
      updateEventReminderAndOtherProperties(createCalendarEventRequest, event);

      // Insert the event into the calendar
      final Calendar.Events.Insert insertRequest = createInsertRequest(createCalendarEventRequest, event);
      if (nonNull(insertRequest)) {
        final Event newEvent = insertRequest.execute();
        // If the new event is successfully created, return its ID and expanded details
        if (nonNull(newEvent)) {
          return GoogleCreateCalendarEventResponse.of(newEvent.getId(), newEvent.getHangoutLink(), requireNonNull(mapToEventExpanded(newEvent)));
        }
      }
    } catch (final IOException ex) {
      final String errorMessage = String.format("Error has occurred while creating an event. Reason: %s", ex.getMessage());
      reporterService.sendMessage(errorMessage, GOOGLE_CALENDAR);
    }
    throw new UnableToCompleteOperationException();
  }

  /**
   * Creates an insert request to add an event to a calendar.
   * The event is associated with a specific calendar and additional options like conference data and update settings are configured.
   *
   * @param createCalendarEventRequest the request containing calendar and event details.
   * @param event the event to be inserted into the calendar.
   * @return a Calendar.Events.Insert request ready to be executed or null if inputs are invalid.
   * @throws IOException if there is an error creating the insert request.
   */
  protected Calendar.Events.Insert createInsertRequest(final CreateCalendarEventRequest createCalendarEventRequest, final Event event) throws IOException {
    if (nonNull(event) && nonNull(createCalendarEventRequest)) {
      // Create the insert request for the given calendar and event
      final Calendar.Events.Insert insert = calendar
        .events()
        .insert(createCalendarEventRequest.getCalendarIdOrName(), event);

      // Set the version for conference data (enabling conference support like Google Meet)
      insert.setConferenceDataVersion(1);
      // Configure the request to send updates to all event participants
      insert.setSendUpdates(EventSendUpdate.all());
      return insert;
    }
    return null;
  }

  /**
   * Updates the basic event information such as title, description, visibility, and guest permissions.
   * These basic details are extracted from the request object and applied to the event.
   *
   * @param createCalendarEventRequest the request containing basic event information.
   * @param event the event to be updated with the provided basic details.
   */
  protected void updateBasicEventInfo(final CreateCalendarEventRequest createCalendarEventRequest, final Event event) {
    if (nonNull(event) && nonNull(createCalendarEventRequest)) {
      // Set event details from the request object
      event.setSummary(createCalendarEventRequest.getTitle());
      // Set the event description from the request
      event.setDescription(createCalendarEventRequest.getDescription());
      // Set the event visibility (e.g., public, private)
      event.setVisibility(createCalendarEventRequest.getVisibility());
      // Configure guest permissions: can guests see each other
      event.setGuestsCanSeeOtherGuests(createCalendarEventRequest.getCanGuestsCanSeeOtherGuests());
      // Configure guest permissions: can guests invite others
      event.setGuestsCanInviteOthers(createCalendarEventRequest.getCanGuestsInviteOtherGuests());
      // Set the event location
      event.setLocation(createCalendarEventRequest.getLocation());
    }
  }

  /**
   * Updates the event with reminders and other properties such as metadata.
   * Custom reminders and extended properties are set based on the provided request.
   *
   * @param createCalendarEventRequest the request containing event reminder and metadata details.
   * @param event the event to be updated with reminders and extended properties.
   */
  protected void updateEventReminderAndOtherProperties(final CreateCalendarEventRequest createCalendarEventRequest, final Event event) {
    // Set event reminders
    final Event.Reminders reminders = new Event.Reminders()
            .setUseDefault(false)
            .setOverrides(createAndReturnEventReminders());
    event.setReminders(reminders);

    // Set extended properties (additional metadata)
    event.setExtendedProperties(new Event.ExtendedProperties().setShared(createCalendarEventRequest.getEventMetaData()));
  }

  /**
   * Updates the event's schedule and attendees or guests based on the provided request.
   * Sets the event start and end times, and assigns the list of attendees or guests.
   *
   * @param createCalendarEventRequest the request containing event schedule and attendee details.
   * @param event the event to be updated with the schedule and attendees or guests. Must not be null.
   */
  protected void updateEventSchedulesAndAttendeesOrGuests(final CreateCalendarEventRequest createCalendarEventRequest, final Event event) {
    if (nonNull(event) && nonNull(createCalendarEventRequest)) {
      // Set event start and end times
      final EventDateTime eventStartDateTime = createEventDateAndTime(createCalendarEventRequest.getStartDateTime(), createCalendarEventRequest.getTimezone());
      event.setStart(eventStartDateTime);

      final EventDateTime eventEndDateTime = createEventDateAndTime(createCalendarEventRequest.getEndDateTime(), createCalendarEventRequest.getTimezone());
      event.setEnd(eventEndDateTime);

      // Set attendees or guests
      final List<EventAttendee> attendees = addOrInviteAttendeesOrGuests(createCalendarEventRequest.getAttendeeOrGuestEmailAddresses());
      event.setAttendees(attendees);
    }
  }

  /**
   * Updates the given event with creator and organizer details based on the provided request.
   * The creator represents the user who created the event, while the organizer typically
   * represents the main contact for the event.
   *
   * @param createCalendarEventRequest the request containing details about the creator and organizer.
   * @param event the event to be updated with creator and organizer information. Must not be null.
   */
  protected void updateEventCreatorAndOrganizerDetails(final CreateCalendarEventRequest createCalendarEventRequest, final Event event) {
    if (nonNull(event) && nonNull(createCalendarEventRequest)) {
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
    }
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
  @Override
  public GoogleCancelCalendarEventResponse cancelEvent(final CancelCalendarEventRequest cancelCalendarEventRequest) {
    try {
      // Retrieve calendar ID and event ID from the cancellation request
      final String calendarId = cancelCalendarEventRequest.getCalendarId();
      final String eventId = cancelCalendarEventRequest.getEventId();

      // Retrieve the event from the calendar
      final Event event = calendar.events().get(calendarId, eventId).execute();

      // If the event exists, set its status to cancelled and update it on the calendar
      if (nonNull(event)) {
        event.setStatus(EventStatus.cancelled());
        // Execute update request and notify all existing approved attendees
        calendar.events()
                .update(calendarId, eventId, event)
                .setSendUpdates(EventSendUpdate.all())
                .execute();

        return GoogleCancelCalendarEventResponse.of(cancelCalendarEventRequest.getEventId(), mapToEventExpanded(event));
      }
      logIfEnabled(log::isErrorEnabled, () -> log.error("Cannot cancel event. Event does not exist or cannot be found. {}", cancelCalendarEventRequest.getEventId()));
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
  @Override
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
          .setSendUpdates(EventSendUpdate.all())
          .execute();

        return GoogleRescheduleCalendarEventResponse.of(rescheduleCalendarEventRequest.getEventId(), mapToEventExpanded(event));
      }
      logIfEnabled(log::isErrorEnabled, () -> log.error("Cannot reschedule event. Event does not exist or cannot be found. {}", rescheduleCalendarEventRequest.getEventId()));
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
  @Override
  public GoogleDeleteCalendarEventResponse deleteEvent(final DeleteCalendarEventRequest deleteCalendarEventRequest) {
    try {
      // Retrieve calendar ID and event ID from the deletion request
      final String calendarId = deleteCalendarEventRequest.calendarId();
      final String eventId = deleteCalendarEventRequest.eventId();

      // Retrieve the event from the calendar
      final Event event = calendar.events().get(calendarId, eventId).execute();

      // If the event exists, delete it from the calendar and notify attendees or guests
      if (nonNull(event)) {
          calendar.events()
            .delete(calendarId, eventId)
            .setSendUpdates(EventSendUpdate.all())
            .execute();

        return GoogleDeleteCalendarEventResponse.of(deleteCalendarEventRequest.eventId(), mapToEventExpanded(event));
      }
    } catch (final IOException ex) {
      final String errorMessage = String.format("Error has occurred while deleting the event. Reason: %s", ex.getMessage());
      reporterService.sendMessage(errorMessage, GOOGLE_CALENDAR);
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
   * @return {@link GooglePatchCalendarEventResponse} the response containing the patched event
   * @throws UnableToCompleteOperationException if the operation cannot be completed
   */
  @MeasureExecutionTime
  @Override
  public GooglePatchCalendarEventResponse patchEvent(final PatchCalendarEventRequest patchCalendarEventRequest) {
    try {
      // Retrieve calendar ID and event ID from the cancellation request
      final String calendarId = patchCalendarEventRequest.getCalendarId();
      final String eventId = patchCalendarEventRequest.getEventId();

      // Create a request to retrieve an event from a calendar
      final RetrieveCalendarEventRequest retrieveCalendarEventRequest = RetrieveCalendarEventRequest.of(calendarId, eventId);
      // Retrieve the event from the calendar
      final GoogleRetrieveCalendarEventResponse googleRetrieveCalendarEventResponse = googleCalendarEventSearchService.retrieveEvent(retrieveCalendarEventRequest);

      // If the event exists, update its title and description and patch it on the calendar
      if (nonNull(googleRetrieveCalendarEventResponse)) {
        // Retrieve calendar event from the response and set new details
        final Event event = googleRetrieveCalendarEventResponse.event();
        event.setSummary(patchCalendarEventRequest.getTitle());
        event.setDescription(patchCalendarEventRequest.getDescription());
        event.setLocation(patchCalendarEventRequest.getLocation());

        // Save event with updated summary, description and other details
        final Event patchedEvent = calendar.events()
                .patch(calendarId, eventId, event)
                .setSendUpdates(EventSendUpdate.all())
                .execute();

        return GooglePatchCalendarEventResponse.of(patchCalendarEventRequest.getEventId(), mapToEventExpanded(patchedEvent));
      }
      logIfEnabled(log::isErrorEnabled, () -> log.error("Cannot patch or update event. Event does not exist or cannot be found. {}", patchCalendarEventRequest.getEventId()));
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
  @Override
  public GoogleCreateInstantCalendarEventResponse createInstantEvent(final CreateInstantCalendarEventRequest createInstantCalendarEventRequest) {
    try {
      // Use the quickAdd feature to create an instant event with the provided title
      final Event event = calendar.events()
              .quickAdd(createInstantCalendarEventRequest.getCalendarId(),
                        createInstantCalendarEventRequest.getTitle())
              .setSendNotifications(createInstantCalendarEventRequest.getSendNotifications())
              .setSendUpdates(EventSendUpdate.all())
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
  @Override
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
        event.setVisibility(updateCalendarEventVisibilityRequest.getVisibility());
        final Event patchedEvent = calendar.events()
                .patch(calendarId, eventId, event)
                .execute();

        return GooglePatchCalendarEventResponse.of(updateCalendarEventVisibilityRequest.getEventId(), mapToEventExpanded(patchedEvent));
      }

      logIfEnabled(log::isErrorEnabled, () -> log.error("Cannot update visibility. Event does not exist or cannot be found. {}", updateCalendarEventVisibilityRequest.getEventId()));
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
  @Override
  public String getDefaultConferenceSolutionName() {
    return GoogleCalendarEventServiceImpl.DEFAULT_CONFERENCE_SOLUTION_NAME;
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


}
