package com.fleencorp.feen.model.response.external.google.calendar.event;

import com.fleencorp.feen.model.response.external.google.calendar.event.base.GoogleCalendarEventResponse;

import static java.util.Objects.nonNull;

public record GoogleCreateInstantCalendarEventResponse(String eventId, GoogleCalendarEventResponse eventResponse) {

  public String getHangoutLink() {
    return nonNull(eventResponse) ? eventResponse.getHangoutLink() : null;
  }

  public static GoogleCreateInstantCalendarEventResponse of(final String eventId, final GoogleCalendarEventResponse eventResponse) {
    return new GoogleCreateInstantCalendarEventResponse(eventId, eventResponse);
  }
}
