package com.fleencorp.feen.model.response.external.google.calendar.event;

import com.fleencorp.feen.model.response.external.google.calendar.event.base.GoogleCalendarEventResponse;

public record GoogleCancelCalendarEventResponse(String eventId, GoogleCalendarEventResponse eventResponse) {

  public static GoogleCancelCalendarEventResponse of(final String eventId, final GoogleCalendarEventResponse eventResponse) {
    return new GoogleCancelCalendarEventResponse(eventId, eventResponse);
  }
}
