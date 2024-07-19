package com.fleencorp.feen.event.publisher;

import com.fleencorp.feen.event.model.PublishMessageRequest;
import com.fleencorp.feen.event.service.PublisherService;
import com.fleencorp.feen.model.event.AddCalendarEventAttendeesEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * StreamEventPublisher is responsible for publishing stream-related events.
 *
 * <p>This class utilizes the {@link ApplicationEventPublisher} to publish events related to stream
 * operations such as adding attendees, creating or updating streams, etc.</p>
 *
 * <p>By using the {@link ApplicationEventPublisher}, the class ensures that events are handled
 * asynchronously and that the event handling logic is decoupled from the main application logic.</p>
 */
@Slf4j
@Component
@Qualifier("streamEvent-applicationEvent")
public class StreamEventPublisher implements PublisherService {

  private final ApplicationEventPublisher eventPublisher;


  /**
   * Constructs a new StreamEventPublisher with the given event publisher.
   *
   * @param eventPublisher The {@link ApplicationEventPublisher} used to publish events.
   */
  public StreamEventPublisher(final ApplicationEventPublisher eventPublisher) {
    this.eventPublisher = eventPublisher;
  }

  @Override
  public void publishMessage(PublishMessageRequest messageRequest) {
    Object message = messageRequest.getMessage();
    switch (message) {
      case AddCalendarEventAttendeesEvent request -> addNewAttendees(request);
      default -> {}
    }
  }

  /**
   * Publishes an event to add new attendees to a calendar event.
   *
   * <p>This method takes an {@link AddCalendarEventAttendeesEvent} object as a parameter and uses
   * the {@link ApplicationEventPublisher} to publish the event.</p>
   *
   * <p>This allows the system to handle adding new attendees to a calendar event asynchronously
   * and decouples the event publication from the actual handling of adding attendees.</p>
   *
   * @param addCalendarEventAttendeesEvent The event object containing the details of the attendees to be added.
   */
  public void addNewAttendees(final AddCalendarEventAttendeesEvent addCalendarEventAttendeesEvent) {
      this.eventPublisher.publishEvent(addCalendarEventAttendeesEvent);
  }
}
