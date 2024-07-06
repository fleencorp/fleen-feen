package com.fleencorp.feen.event.publisher;

import com.fleencorp.feen.model.event.AddCalendarEventAttendeesEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class StreamEventPublisher {

  private final ApplicationEventPublisher eventPublisher;

  public StreamEventPublisher(final ApplicationEventPublisher eventPublisher) {
    this.eventPublisher = eventPublisher;
  }

  public void addNewAttendees(final AddCalendarEventAttendeesEvent addCalendarEventAttendeesEvent) {
      this.eventPublisher.publishEvent(addCalendarEventAttendeesEvent);
  }
}
