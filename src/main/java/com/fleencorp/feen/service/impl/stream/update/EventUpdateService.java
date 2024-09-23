package com.fleencorp.feen.service.impl.stream.update;

import com.fleencorp.feen.constant.base.ResultType;
import com.fleencorp.feen.event.broadcast.BroadcastService;
import com.fleencorp.feen.event.model.stream.EventStreamCreatedResult;
import com.fleencorp.feen.model.domain.stream.FleenStream;
import com.fleencorp.feen.model.request.calendar.event.*;
import com.fleencorp.feen.model.response.external.google.calendar.event.*;
import com.fleencorp.feen.repository.stream.FleenStreamRepository;
import com.fleencorp.feen.service.impl.external.google.calendar.GoogleCalendarEventService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation for updating events in both the local database and Google Calendar.
 *
 * <p>This service handles various operations related to events, such as creating, updating, deleting, and rescheduling events.
 * It interacts with both the local `FleenStreamRepository` and the external `GoogleCalendarEventService` to synchronize event details.</p>
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
@Service
@Slf4j
public class EventUpdateService {

  private final FleenStreamRepository fleenStreamRepository;
  private final GoogleCalendarEventService googleCalendarEventService;
  private final BroadcastService broadcastService;

  /**
   * Constructs an instance of {@code EventUpdateServiceImpl}.
   *
   * @param fleenStreamRepository the repository for managing FleenStream entities
   * @param googleCalendarEventService the service for interacting with Google Calendar events
   */
  public EventUpdateService(
      final FleenStreamRepository fleenStreamRepository,
      final GoogleCalendarEventService googleCalendarEventService,
      final BroadcastService broadcastService) {
    this.fleenStreamRepository = fleenStreamRepository;
    this.googleCalendarEventService = googleCalendarEventService;
    this.broadcastService = broadcastService;
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
  @Transactional
  public void createEventInGoogleCalendar(final FleenStream stream, final CreateCalendarEventRequest createCalendarEventRequest) {
    // Create an event using an external service (Google Calendar)
    final GoogleCreateCalendarEventResponse googleCreateCalendarEventResponse = googleCalendarEventService.createEvent(createCalendarEventRequest);
    // Update the stream with the event ID and HTML link
    stream.updateDetails(googleCreateCalendarEventResponse.getEventId(), googleCreateCalendarEventResponse.getEvent().getHangoutLink());
    fleenStreamRepository.save(stream);

    // Create an event stream created result
    final EventStreamCreatedResult eventStreamCreatedResult = EventStreamCreatedResult
      .of(stream.getMember().getMemberId(),
          stream.getStreamId(),
          stream.getExternalId(),
          stream.getStreamLink(),
          ResultType.EVENT_STREAM_CREATED);
    // Broadcast the event creation result
    broadcastService.broadcastEventCreated(eventStreamCreatedResult);
  }

  /**
   * Asynchronously creates an instant event in Google Calendar and updates the FleenStream entity with the event details.
   *
   * @param stream the FleenStream entity to update with the new instant event details
   * @param createInstantCalendarEventRequest the request object containing the details for the instant event to be created in Google Calendar
   */
  @Async
  @Transactional
  public void createInstantEventInGoogleCalendar(final FleenStream stream, final CreateInstantCalendarEventRequest createInstantCalendarEventRequest) {
    // Create an instant event using an external service (Google Calendar)
    final GoogleCreateInstantCalendarEventResponse googleCreateInstantCalendarEventResponse = googleCalendarEventService.createInstantEvent(createInstantCalendarEventRequest);
    // Update the stream with the event ID and HTML link
    stream.updateDetails(googleCreateInstantCalendarEventResponse.getEventId(), googleCreateInstantCalendarEventResponse.getEvent().getHangoutLink());
    fleenStreamRepository.save(stream);
  }

  /**
   * Asynchronously updates an event in Google Calendar and updates the FleenStream entity with the new event details.
   *
   * @param stream the FleenStream entity to update with the new event details
   * @param patchCalendarEventRequest the request object containing the details for the event to be updated in Google Calendar
   */
  @Async
  @Transactional
  public void updateEventInGoogleCalendar(final FleenStream stream, final PatchCalendarEventRequest patchCalendarEventRequest) {
    // Update the event details using an external service (Google Calendar)
    final GooglePatchCalendarEventResponse googlePatchCalendarEventResponse = googleCalendarEventService.patchEvent(patchCalendarEventRequest);
    // Update the stream with the event ID and HTML link
    stream.setExternalId(googlePatchCalendarEventResponse.getEventId());
    stream.setStreamLink(googlePatchCalendarEventResponse.getEvent().getHangoutLink());

    fleenStreamRepository.save(stream);
  }

  /**
   * Asynchronously deletes an event from Google Calendar and logs the deletion.
   *
   * @param deleteCalendarEventRequest the request object containing the details for the event to be deleted from Google Calendar
   */
  @Async
  public void deleteEventInGoogleCalendar(final DeleteCalendarEventRequest deleteCalendarEventRequest) {
    // Delete the event using an external service (Google Calendar)
    final GoogleDeleteCalendarEventResponse googleDeleteCalendarEventResponse = googleCalendarEventService.deleteEvent(deleteCalendarEventRequest);
    log.info("Deleted event: {}", googleDeleteCalendarEventResponse.getEventId());
  }

  /**
   * Asynchronously cancels an event in Google Calendar and logs the cancellation.
   *
   * @param cancelCalendarEventRequest the request object containing the details for the event to be canceled in Google Calendar
   */
  @Async
  public void cancelEventInGoogleCalendar(final CancelCalendarEventRequest cancelCalendarEventRequest) {
    // Cancel the event using an external service (Google Calendar)
    final GoogleCancelCalendarEventResponse googleCancelCalendarEventResponse = googleCalendarEventService.cancelEvent(cancelCalendarEventRequest);
    log.info("Canceled event: {}", googleCancelCalendarEventResponse.getEventId());
  }

  /**
   * Asynchronously reschedules an event in Google Calendar and logs the rescheduling.
   *
   * @param rescheduleCalendarEventRequest the request object containing the details for the event to be rescheduled in Google Calendar
   */
  @Async
  public void rescheduleEventInGoogleCalendar(final RescheduleCalendarEventRequest rescheduleCalendarEventRequest) {
    // Reschedule the event using an external service (Google Calendar)
    final GoogleRescheduleCalendarEventResponse googleRescheduleCalendarEventResponse = googleCalendarEventService.rescheduleEvent(rescheduleCalendarEventRequest);
    log.info("Rescheduled event: {}", googleRescheduleCalendarEventResponse.getEventId());
  }

  /**
   * Asynchronously adds a new attendee to a Google Calendar event and logs the operation.
   *
   * @param addNewEventAttendeeRequest the request object containing the details of the new attendee to be added to the Google Calendar event
   */
  @Async
  public void addNewAttendeeToCalendarEvent(final AddNewEventAttendeeRequest addNewEventAttendeeRequest) {
    // Add a user as an attendee to the event using an external service (Google Calendar)
    final GoogleAddNewCalendarEventAttendeeResponse googleAddNewCalendarEventAttendeeResponse = googleCalendarEventService.addNewAttendeeToCalendarEvent(addNewEventAttendeeRequest);
    log.info("Attendee join event: {}", googleAddNewCalendarEventAttendeeResponse.getEventId());
  }

  /**
   * Updates the visibility of a calendar event using an external service (Google Calendar).
   *
   * @param updateCalendarEventVisibilityRequest the request containing the details for updating event visibility
   * @see GoogleCalendarEventService#updateEventVisibility(UpdateCalendarEventVisibilityRequest)
   */
  @Async
  public void updateEventVisibility(final UpdateCalendarEventVisibilityRequest updateCalendarEventVisibilityRequest) {
    // Update an event visibility using an external service (Google Calendar)
    final GooglePatchCalendarEventResponse googlePatchCalendarEventResponse = googleCalendarEventService.updateEventVisibility(updateCalendarEventVisibilityRequest);
    log.info("Updated event visibility: {}", googlePatchCalendarEventResponse);
  }

  /**
   * Handles the process of marking an attendee as not attending an event.
   * This involves interacting with an external service (Google Calendar) to update the event details.
   *
   * @param notAttendingEventRequest The request containing details about the event and the attendee to be removed.
   */
  @Async
  public void notAttendingEvent(final NotAttendingEventRequest notAttendingEventRequest) {
// Call the external service to remove the attendee from the event in Google Calendar
    final GoogleRetrieveCalendarEventResponse googleRetrieveCalendarEventResponse = googleCalendarEventService.notAttendingEvent(notAttendingEventRequest);
    // Log the response indicating the attendee has been removed from the event
    log.info("Remove attendee from event: {}", googleRetrieveCalendarEventResponse);
  }

}
