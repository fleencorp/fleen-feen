package com.fleencorp.feen.model.response.external.google.calendar.event;

import com.fleencorp.feen.model.response.external.google.calendar.event.base.GoogleCalendarEventResponse;

public record GoogleCreateCalendarEventResponse(String eventId, String eventLinkOrUri, GoogleCalendarEventResponse eventResponse) {

  public static GoogleCreateCalendarEventResponse of(final String eventId, final String eventLinkOrUri, final GoogleCalendarEventResponse eventResponse) {
    return new GoogleCreateCalendarEventResponse(eventId, eventLinkOrUri, eventResponse);
  }
}
