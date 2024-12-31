package com.fleencorp.feen.model.response.external.google.calendar.event;

import com.fleencorp.feen.model.response.external.google.calendar.event.base.GoogleCalendarEventResponse;

public record GoogleAddNewCalendarEventAttendeesResponse(String eventId, GoogleCalendarEventResponse eventResponse) {

  public static GoogleAddNewCalendarEventAttendeesResponse of(final String eventId, final GoogleCalendarEventResponse eventResponse) {
    return new GoogleAddNewCalendarEventAttendeesResponse(eventId, eventResponse);
  }
}
