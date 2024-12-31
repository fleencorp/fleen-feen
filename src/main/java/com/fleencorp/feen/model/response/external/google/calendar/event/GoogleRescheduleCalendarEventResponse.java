package com.fleencorp.feen.model.response.external.google.calendar.event;

import com.fleencorp.feen.model.response.external.google.calendar.event.base.GoogleCalendarEventResponse;

public record GoogleRescheduleCalendarEventResponse(String eventId, GoogleCalendarEventResponse eventResponse) {

  public static GoogleRescheduleCalendarEventResponse of(final String eventId, final GoogleCalendarEventResponse eventResponse) {
    return new GoogleRescheduleCalendarEventResponse(eventId, eventResponse);
  }
}
