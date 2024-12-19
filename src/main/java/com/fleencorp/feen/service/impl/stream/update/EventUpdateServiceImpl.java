package com.fleencorp.feen.service.impl.stream.update;

import com.fleencorp.feen.constant.base.ResultType;
import com.fleencorp.feen.event.broadcast.BroadcastService;
import com.fleencorp.feen.event.model.stream.EventStreamCreatedResult;
import com.fleencorp.feen.mapper.stream.StreamMapper;
import com.fleencorp.feen.model.domain.stream.FleenStream;
import com.fleencorp.feen.model.request.calendar.event.*;
import com.fleencorp.feen.model.request.chat.space.message.GoogleChatSpaceMessageRequest;
import com.fleencorp.feen.model.response.external.google.calendar.event.*;
import com.fleencorp.feen.repository.stream.FleenStreamRepository;
import com.fleencorp.feen.service.external.google.calendar.event.GoogleCalendarEventService;
import com.fleencorp.feen.service.external.google.chat.GoogleChatService;
import com.fleencorp.feen.service.impl.external.google.calendar.attendee.GoogleCalendarAttendeeServiceImpl;
import com.fleencorp.feen.service.stream.update.EventUpdateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.fleencorp.feen.util.LoggingUtil.logIfEnabled;
import static java.util.Objects.requireNonNull;


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
public class EventUpdateServiceImpl implements EventUpdateService {

  private final FleenStreamRepository streamRepository;
  private final GoogleCalendarAttendeeServiceImpl googleCalendarAttendeeService;
  private final GoogleCalendarEventService googleCalendarEventService;
  private final GoogleChatService googleChatService;
  private final BroadcastService broadcastService;
  private final StreamMapper streamMapper;

  /**
   * Constructs an EventUpdateService with the necessary dependencies.
   *
   * @param streamRepository The repository for handling FleenStream data.
   * @param streamMapper The mapper for converting FleenStream entities to response objects.
   * @param broadcastService The service for managing broadcast-related tasks.
   * @param googleCalendarEventService The service for interacting with Google Calendar events.
   * @param googleChatService The service for sending messages to Google Chat.
   */
  public EventUpdateServiceImpl(
      final FleenStreamRepository streamRepository,
      final BroadcastService broadcastService,
      final GoogleCalendarAttendeeServiceImpl googleCalendarAttendeeService,
      final GoogleCalendarEventService googleCalendarEventService,
      final GoogleChatService googleChatService,
      final StreamMapper streamMapper) {
    this.streamRepository = streamRepository;
    this.broadcastService = broadcastService;
    this.googleCalendarEventService = googleCalendarEventService;
    this.googleCalendarAttendeeService = googleCalendarAttendeeService;
    this.googleChatService = googleChatService;
    this.streamMapper = streamMapper;
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
    stream.updateDetails(googleCreateCalendarEventResponse.eventId(), googleCreateCalendarEventResponse.eventLinkOrUri());
    streamRepository.save(stream);
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
   * Creates a Google Calendar event for the given stream and announces it in the associated chat space.
   *
   * <p>This method first creates an event in Google Calendar using the provided {@link CreateCalendarEventRequest},
   * and then sends an announcement message with event details to the associated chat space.</p>
   *
   * @param stream                    The FleenStream instance containing event and chat space details.
   * @param createCalendarEventRequest The request containing details for creating the Google Calendar event.
   */
  @Async
  @Override
  @Transactional
  public void createEventInGoogleCalendarAndAnnounceInSpace(final FleenStream stream, final CreateCalendarEventRequest createCalendarEventRequest) {
    // Create event in Google Calendar
    createEventInGoogleCalendar(stream, createCalendarEventRequest);

    // Prepare the request to send a calendar event message to the chat space
    final GoogleChatSpaceMessageRequest googleChatSpaceMessageRequest = GoogleChatSpaceMessageRequest.ofEventOrStream(
      stream.getSpaceIdOrName(),
      requireNonNull(streamMapper.toFleenStreamResponseNoJoinStatus(stream))
    );

    // Send the event message to the chat space
    googleChatService.createCalendarEventMessageAndSendToChatSpace(googleChatSpaceMessageRequest);

    // Create add new attendee request to add the organizer as an attendee of the event
    addOrganizerOrAnyoneAsAttendeeOrGuestOfEvent(
      createCalendarEventRequest.getCalendarIdOrName(),
      createCalendarEventRequest.getEventId(),
      createCalendarEventRequest.getCreatorEmail(),
      createCalendarEventRequest.getOrganizerDisplayName()
    );
  }

  /**
   * Asynchronously creates an instant event in Google Calendar and updates the FleenStream entity with the event details.
   *
   * @param stream the FleenStream entity to update with the new instant event details
   * @param createInstantCalendarEventRequest the request object containing the details for the instant event to be created in Google Calendar
   */
  @Async
  @Override
  @Transactional
  public void createInstantEventInGoogleCalendar(final FleenStream stream, final CreateInstantCalendarEventRequest createInstantCalendarEventRequest) {
    // Create an instant event using an external service (Google Calendar)
    final GoogleCreateInstantCalendarEventResponse googleCreateInstantCalendarEventResponse = googleCalendarEventService.createInstantEvent(createInstantCalendarEventRequest);
    // Update the stream with the event ID and HTML link
    stream.updateDetails(googleCreateInstantCalendarEventResponse.eventId(), googleCreateInstantCalendarEventResponse.getHangoutLink());
    streamRepository.save(stream);
  }

  /**
   * Asynchronously updates an event in Google Calendar and updates the FleenStream entity with the new event details.
   *
   * @param stream the FleenStream entity to update with the new event details
   * @param patchCalendarEventRequest the request object containing the details for the event to be updated in Google Calendar
   */
  @Async
  @Override
  @Transactional
  public void updateEventInGoogleCalendar(final FleenStream stream, final PatchCalendarEventRequest patchCalendarEventRequest) {
    // Update the event details using an external service (Google Calendar)
    final GooglePatchCalendarEventResponse googlePatchCalendarEventResponse = googleCalendarEventService.patchEvent(patchCalendarEventRequest);
    // Update the stream with the event ID and HTML link
    stream.setExternalId(googlePatchCalendarEventResponse.eventId());
    stream.setStreamLink(googlePatchCalendarEventResponse.getHangoutLink());

    streamRepository.save(stream);
  }

  /**
   * Asynchronously deletes an event from Google Calendar and logs the deletion.
   *
   * @param deleteCalendarEventRequest the request object containing the details for the event to be deleted from Google Calendar
   */
  @Async
  @Override
  public void deleteEventInGoogleCalendar(final DeleteCalendarEventRequest deleteCalendarEventRequest) {
    // Delete the event using an external service (Google Calendar)
    final GoogleDeleteCalendarEventResponse googleDeleteCalendarEventResponse = googleCalendarEventService.deleteEvent(deleteCalendarEventRequest);
    logIfEnabled(log::isInfoEnabled, () -> log.info("Deleted event: {}", googleDeleteCalendarEventResponse.eventId()));
  }

  /**
   * Asynchronously cancels an event in Google Calendar and logs the cancellation.
   *
   * @param cancelCalendarEventRequest the request object containing the details for the event to be canceled in Google Calendar
   */
  @Async
  @Override
  public void cancelEventInGoogleCalendar(final CancelCalendarEventRequest cancelCalendarEventRequest) {
    // Cancel the event using an external service (Google Calendar)
    final GoogleCancelCalendarEventResponse googleCancelCalendarEventResponse = googleCalendarEventService.cancelEvent(cancelCalendarEventRequest);
    logIfEnabled(log::isInfoEnabled, () -> log.info("Canceled event: {}", googleCancelCalendarEventResponse.eventResponse()));
  }

  /**
   * Asynchronously reschedules an event in Google Calendar and logs the rescheduling.
   *
   * @param rescheduleCalendarEventRequest the request object containing the details for the event to be rescheduled in Google Calendar
   */
  @Async
  @Override
  public void rescheduleEventInGoogleCalendar(final RescheduleCalendarEventRequest rescheduleCalendarEventRequest) {
    // Reschedule the event using an external service (Google Calendar)
    final GoogleRescheduleCalendarEventResponse googleRescheduleCalendarEventResponse = googleCalendarEventService.rescheduleEvent(rescheduleCalendarEventRequest);
    logIfEnabled(log::isInfoEnabled, () -> log.info("Rescheduled event: {}", googleRescheduleCalendarEventResponse.eventId()));
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

  /**
   * Updates the visibility of a calendar event using an external service (Google Calendar).
   *
   * @param updateCalendarEventVisibilityRequest the request containing the details for updating event visibility
   * @see GoogleCalendarEventService#updateEventVisibility(UpdateCalendarEventVisibilityRequest)
   */
  @Async
  @Override
  public void updateEventVisibility(final UpdateCalendarEventVisibilityRequest updateCalendarEventVisibilityRequest) {
    // Update an event visibility using an external service (Google Calendar)
    final GooglePatchCalendarEventResponse googlePatchCalendarEventResponse = googleCalendarEventService.updateEventVisibility(updateCalendarEventVisibilityRequest);
    logIfEnabled(log::isInfoEnabled, () -> log.info("Updated event visibility: {}", googlePatchCalendarEventResponse));
  }

  /**
   * Handles the process of marking an attendee as not attending an event.
   * This involves interacting with an external service (Google Calendar) to update the event details.
   *
   * @param notAttendingEventRequest The request containing details about the event and the attendee to be removed.
   */
  @Async
  @Override
  public void notAttendingEvent(final NotAttendingEventRequest notAttendingEventRequest) {
    // Call the external service to remove the attendee from the event in Google Calendar
    final GoogleRetrieveCalendarEventResponse googleRetrieveCalendarEventResponse = googleCalendarAttendeeService.notAttendingEvent(notAttendingEventRequest);
    // Log the response indicating the attendee has been removed from the event
    logIfEnabled(log::isInfoEnabled, () -> log.info("Remove attendee from event: {}", googleRetrieveCalendarEventResponse));
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
      organizerDisplayName
    );
    // Call the external service (e.g., Google Calendar) to add the organizer as an attendee
    addNewAttendeeToCalendarEvent(addNewEventAttendeeRequest);
  }

  /**
   * Broadcasts an event or stream creation notification to notify listeners or external systems.
   * It creates an event stream created result and sends it using the broadcast service.
   *
   * @param stream the event or stream object that has been created and will be broadcasted
   */
  @Override
  public void broadcastEventOrStreamCreated(final FleenStream stream) {
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

}
