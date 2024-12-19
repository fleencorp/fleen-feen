package com.fleencorp.feen.model.response.external.google.calendar.event;

import com.fleencorp.feen.model.response.external.google.calendar.event.base.GoogleCalendarEventResponse;

public record GoogleDeleteCalendarEventResponse(String eventId, GoogleCalendarEventResponse eventResponse) {

  public static GoogleDeleteCalendarEventResponse of(final String eventId, final GoogleCalendarEventResponse eventResponse) {
    return new GoogleDeleteCalendarEventResponse(eventId, eventResponse);
  }
}
