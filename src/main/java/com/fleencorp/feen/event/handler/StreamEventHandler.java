package com.fleencorp.feen.event.handler;

import com.fleencorp.feen.model.event.AddCalendarEventAttendeesEvent;
import com.fleencorp.feen.model.request.calendar.event.AddNewEventAttendeesRequest;
import com.fleencorp.feen.model.response.external.google.calendar.event.GoogleAddNewCalendarEventAttendeesResponse;
import com.fleencorp.feen.service.external.google.calendar.GoogleCalendarEventService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.HashSet;
import java.util.concurrent.CompletableFuture;

import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;

/**
 * Event handler class responsible for handling stream-related events and interacting with Google Calendar.
 *
 * <p>This class provides methods to handle various stream-related events and integrates with the Google Calendar
 * service to perform actions such as adding attendees to calendar events.</p>
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
@Slf4j
@Service
public class StreamEventHandler {

  private final GoogleCalendarEventService googleCalendarEventService;

  /**
   * Constructs a new StreamEventHandler with the specified GoogleCalendarEventService.
   *
   * @param googleCalendarEventService the service used to interact with Google Calendar for event-related operations
   */
  public StreamEventHandler(final GoogleCalendarEventService googleCalendarEventService) {
    this.googleCalendarEventService = googleCalendarEventService;
  }

  /**
   * Handles the AddCalendarEventAttendeesEvent by adding new attendees to a calendar event.
   *
   * <p>This method is annotated with @TransactionalEventListener to listen for AddCalendarEventAttendeesEvent events.
   * When such an event is triggered, it creates an AddNewEventAttendeesRequest using the details from the event,
   * and then calls the googleCalendarEventService to add the new attendees to the specified calendar event.</p>
   *
   * @param event the AddCalendarEventAttendeesEvent containing the details for adding new attendees
   */
  @TransactionalEventListener(phase = AFTER_COMMIT)
  @Async
  public CompletableFuture<Void> addNewAttendees(final AddCalendarEventAttendeesEvent event) {
    return CompletableFuture.runAsync(() -> {
      // Create a request to add new attendees to the calendar event
      final AddNewEventAttendeesRequest addNewEventAttendeesRequest = AddNewEventAttendeesRequest.builder()
              .calendarId(event.getCalendarId())
              .eventId(event.getEventId())
              .attendeesOrGuestsEmailAddresses(new HashSet<>(event.getAttendeesOrGuestsEmailAddresses()))
              .build();

      // Call the Google Calendar API Service to add the new attendees to the event
      final GoogleAddNewCalendarEventAttendeesResponse googleAddNewCalendarEventAttendeesResponse = googleCalendarEventService.addNewAttendeesToCalendarEvent(addNewEventAttendeesRequest);
      log.info("Added attendees: {}", googleAddNewCalendarEventAttendeesResponse);
    });
  }
}
