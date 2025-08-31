package com.fleencorp.feen.stream.service.impl.event;

import com.fleencorp.feen.calendar.model.request.event.create.AddNewEventAttendeeRequest;
import com.fleencorp.feen.calendar.model.request.event.create.CreateCalendarEventRequest;
import com.fleencorp.feen.common.constant.base.ResultType;
import com.fleencorp.feen.common.event.broadcast.BroadcastService;
import com.fleencorp.feen.common.event.model.stream.EventStreamCreatedResult;
import com.fleencorp.feen.model.response.external.google.calendar.event.GoogleAddNewCalendarEventAttendeeResponse;
import com.fleencorp.feen.model.response.external.google.calendar.event.GoogleCreateCalendarEventResponse;
import com.fleencorp.feen.service.external.google.calendar.attendee.GoogleCalendarAttendeeService;
import com.fleencorp.feen.service.external.google.calendar.event.GoogleCalendarEventService;
import com.fleencorp.feen.stream.model.domain.FleenStream;
import com.fleencorp.feen.stream.service.common.StreamOperationsService;
import com.fleencorp.feen.stream.service.event.OtherEventUpdateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.fleencorp.feen.common.util.common.LoggingUtil.logIfEnabled;

/**
 * Implementation of the {@code OtherEventUpdateService} interface.
 *
 * <p>This class provides functionality for handling updates related to other types of events,
 * specifically through interaction with Google Calendar attendee data. It allows updating and managing
 * attendee information for events.</p>
 *
 * @author Yusuf Àlàmú Musa
 * @version 1.0
 */
@Slf4j
@Service
public class OtherEventUpdateServiceImpl implements OtherEventUpdateService {

  private final BroadcastService broadcastService;
  private final GoogleCalendarAttendeeService googleCalendarAttendeeService;
  private final GoogleCalendarEventService googleCalendarEventService;
  private final StreamOperationsService streamOperationsService;

  public OtherEventUpdateServiceImpl(
      final BroadcastService broadcastService,
      final GoogleCalendarAttendeeService googleCalendarAttendeeService,
      final GoogleCalendarEventService googleCalendarEventService,
      final StreamOperationsService streamOperationsService) {
    this.broadcastService = broadcastService;
    this.googleCalendarAttendeeService = googleCalendarAttendeeService;
    this.googleCalendarEventService = googleCalendarEventService;
    this.streamOperationsService = streamOperationsService;
  }

  /**
   * Asynchronously creates an event in Google Calendar and updates the FleenStream with the event details.
   *
   * <p>This method uses an external service (Google Calendar) to create an event and updates the given
   * FleenStream with the event's ID and HTML link. After updating the FleenStream, it saves the stream
   * to the repository. Additionally, it broadcasts an event creation result using the broadcast service.</p>
   *
   * @param stream the FleenStream object to be updated with the event details.
   * @param createCalendarEventRequest the request object containing details for creating the calendar event.
   */
  @Async
  @Override
  @Transactional
  public void createEventInGoogleCalendar(final FleenStream stream, final CreateCalendarEventRequest createCalendarEventRequest) {
    // Create an event using an external service (Google Calendar)
    final GoogleCreateCalendarEventResponse googleCreateCalendarEventResponse = googleCalendarEventService.createEvent(createCalendarEventRequest);
    // Update the stream with the event ID and HTML link
    stream.update(googleCreateCalendarEventResponse.eventId(), googleCreateCalendarEventResponse.eventLinkOrUri());
    // Save it
    streamOperationsService.save(stream);
    // Set the event ID from the created event to to be reused
    createCalendarEventRequest.update(googleCreateCalendarEventResponse.eventId(), googleCreateCalendarEventResponse.eventLinkOrUri());

    broadcastEventOrStreamCreated(stream);

    // Adds the organizer or another specified individual as an attendee or guest of the event
    addOrganizerOrAnyoneAsAttendeeOrGuestOfEvent(
      createCalendarEventRequest.getCalendarIdOrName(),
      googleCreateCalendarEventResponse.eventId(),
      createCalendarEventRequest.getOrganizerEmail(),
      createCalendarEventRequest.getOrganizerDisplayName()
    );
  }

  /**
   * Adds the organizer or another specified individual as an attendee or guest of the event
   * based on the provided event and organizer details.
   *
   * @param calendarId         the ID of the calendar where the event is scheduled
   * @param eventId            the ID of the event where the organizer will be added as an attendee
   * @param organizerEmail     the email address of the organizer or individual to be added
   * @param organizerDisplayName the display name of the organizer or individual to be added
   */
  @Override
  public void addOrganizerOrAnyoneAsAttendeeOrGuestOfEvent(final String calendarId, final String eventId, final String organizerEmail, final String organizerDisplayName) {
    // Create a request object to add the organizer as an attendee of the event
    final AddNewEventAttendeeRequest addNewEventAttendeeRequest = AddNewEventAttendeeRequest.of(
      calendarId,
      eventId,
      organizerEmail,
      organizerDisplayName,
      true
    );

    // Call the external service (e.g., Google Calendar) to add the organizer as an attendee
    addNewAttendeeToCalendarEvent(addNewEventAttendeeRequest);
  }

  /**
   * Broadcasts an event or stream creation notification to notify listeners or external systems.
   * It creates an event stream created result and sends it using the broadcast service.
   *
   * @param stream the event or stream object that has been created and will be broadcast
   */
  @Override
  public void broadcastEventOrStreamCreated(final FleenStream stream) {
    // Create an event stream created result
    final EventStreamCreatedResult eventStreamCreatedResult = EventStreamCreatedResult.of(
        stream.getOrganizerId(),
        stream.getStreamId(),
        stream.getExternalId(),
        stream.getStreamLink(),
        ResultType.EVENT_STREAM_CREATED
    );

    // Broadcast the event creation result
    broadcastService.broadcastEventCreated(eventStreamCreatedResult);
  }

  /**
   * Asynchronously adds a new attendee to a Google Calendar event and logs the operation.
   *
   * @param addNewEventAttendeeRequest the request object containing the details of the new attendee to be added to the Google Calendar event
   */
  @Async
  @Override
  public void addNewAttendeeToCalendarEvent(final AddNewEventAttendeeRequest addNewEventAttendeeRequest) {
    // Add a user as an attendee to the event using an external service (Google Calendar)
    final GoogleAddNewCalendarEventAttendeeResponse googleAddNewCalendarEventAttendeeResponse = googleCalendarAttendeeService.addNewAttendeeToCalendarEvent(addNewEventAttendeeRequest);
    logIfEnabled(log::isInfoEnabled, () -> log.info("Attendee join event: {}", googleAddNewCalendarEventAttendeeResponse.eventId()));
  }
}
