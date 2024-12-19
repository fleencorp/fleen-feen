package com.fleencorp.feen.model.response.external.google.calendar.event;

import com.fleencorp.feen.model.response.external.google.calendar.event.base.GoogleCalendarEventResponse;

public record GoogleAddNewCalendarEventAttendeeResponse(String eventId, String userEmailAddress, GoogleCalendarEventResponse eventResponse) {

  public static GoogleAddNewCalendarEventAttendeeResponse of(final String eventId, final String userEmailAddress, final GoogleCalendarEventResponse eventResponse) {
    return new GoogleAddNewCalendarEventAttendeeResponse(eventId, userEmailAddress, eventResponse);
  }
}
