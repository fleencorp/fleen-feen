package com.fleencorp.feen.model.response.external.google.calendar.event;

import com.fleencorp.feen.model.response.external.google.calendar.event.base.GoogleCalendarEventResponse;
import com.google.api.services.calendar.model.Event;

public record GoogleRetrieveCalendarEventResponse(String eventId, GoogleCalendarEventResponse eventResponse, Event event) {

  public static GoogleRetrieveCalendarEventResponse of(final String eventId, final GoogleCalendarEventResponse eventResponse, final Event event) {
    return new GoogleRetrieveCalendarEventResponse(eventId, eventResponse, event);
  }
}
