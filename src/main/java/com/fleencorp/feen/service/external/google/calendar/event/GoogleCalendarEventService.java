package com.fleencorp.feen.service.external.google.calendar.event;

import com.fleencorp.feen.calendar.model.request.event.create.CreateCalendarEventRequest;
import com.fleencorp.feen.calendar.model.request.event.create.CreateInstantCalendarEventRequest;
import com.fleencorp.feen.calendar.model.request.event.update.*;
import com.fleencorp.feen.constant.external.google.calendar.ConferenceSolutionType;
import com.fleencorp.feen.model.response.external.google.calendar.event.*;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.fleencorp.feen.util.DateTimeUtil.toMilliseconds;
import static com.fleencorp.feen.util.external.google.GoogleApiUtil.toDateTime;
import static java.util.Objects.nonNull;

public interface GoogleCalendarEventService {

  GoogleCreateCalendarEventResponse createEvent(CreateCalendarEventRequest createCalendarEventRequest);

  GoogleCancelCalendarEventResponse cancelEvent(CancelCalendarEventRequest cancelCalendarEventRequest);

  GoogleRescheduleCalendarEventResponse rescheduleEvent(RescheduleCalendarEventRequest rescheduleCalendarEventRequest);

  GoogleDeleteCalendarEventResponse deleteEvent(DeleteCalendarEventRequest deleteCalendarEventRequest);

  GooglePatchCalendarEventResponse patchEvent(PatchCalendarEventRequest patchCalendarEventRequest);

  GoogleCreateInstantCalendarEventResponse createInstantEvent(CreateInstantCalendarEventRequest createInstantCalendarEventRequest);

  GooglePatchCalendarEventResponse updateEventVisibility(UpdateCalendarEventVisibilityRequest updateCalendarEventVisibilityRequest);

  String getDefaultConferenceSolutionName();

  /**
   * Updates the given event with conference details, including the conference solution and
   * conference request data. This method ensures that conference settings like solution type
   * and request IDs are set appropriately for the event.
   *
   * @param createCalendarEventRequest the request object containing details about the calendar event
   *                                   including start time, end time, and timezone.
   * @param event the event to be updated with the conference data. Must not be null.
   */
  default void updateEventConferenceDetails(final CreateCalendarEventRequest createCalendarEventRequest, final Event event) {
    if (nonNull(event) && nonNull(createCalendarEventRequest)) {
      // Initialize and set the conference solution name
      final ConferenceSolution conferenceSolution = new ConferenceSolution();
      conferenceSolution.setName(getDefaultConferenceSolutionName());

      // Initialize and set the conference solution key type
      final ConferenceSolutionKey conferenceSolutionKey = new ConferenceSolutionKey();
      conferenceSolutionKey.setType(ConferenceSolutionType.getDefault());

      // Create and set the conference request ID based on event start/end times and timezone
      final CreateConferenceRequest createConferenceRequest = new CreateConferenceRequest();
      createConferenceRequest.setRequestId(
        createConferenceRequestId(
          createCalendarEventRequest.getStartDateTime(),
          createCalendarEventRequest.getEndDateTime(),
          createCalendarEventRequest.getTimezone()
        ));
      createConferenceRequest.setConferenceSolutionKey(conferenceSolutionKey);

      // Initialize conference data with the created request and solution
      final ConferenceData conferenceData = new ConferenceData();
      conferenceData.setCreateRequest(createConferenceRequest);

      // Attach the conference data to the event
      conferenceData.setConferenceSolution(conferenceSolution);
      event.setConferenceData(conferenceData);
    }
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
  default List<EventReminder> createAndReturnEventReminders() {
    final List<EventReminder> eventReminders = new ArrayList<>();
    final int aDayOrOneDayInHours = 24 * 60;
    eventReminders.add(new EventReminder().setMethod("popup").setMinutes(10));
    eventReminders.add(new EventReminder().setMethod("email").setMinutes(aDayOrOneDayInHours));
    eventReminders.add(new EventReminder().setMethod("popup").setMinutes(aDayOrOneDayInHours));
    return eventReminders;
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
  default String createConferenceRequestId(final LocalDateTime startDate, final LocalDateTime endDate, final String timezone) {
    return toMilliseconds(startDate, timezone) + "-" + toMilliseconds(endDate, timezone);
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
  default EventDateTime createEventDateAndTime(final LocalDateTime localDateTime, final String timezone) {
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
  default EventDateTime createEventDateAndTime(final DateTime dateTime, final String timezone) {
    final EventDateTime eventDateTime = new EventDateTime();
    eventDateTime.setDateTime(dateTime);
    eventDateTime.setTimeZone(timezone);

    return eventDateTime;
  }

}
