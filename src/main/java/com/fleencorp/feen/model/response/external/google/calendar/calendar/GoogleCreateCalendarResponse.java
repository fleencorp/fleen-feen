package com.fleencorp.feen.model.response.external.google.calendar.calendar;

import com.fleencorp.feen.model.response.external.google.calendar.calendar.base.GoogleCalendarResponse;


public record GoogleCreateCalendarResponse(String calendarId, GoogleCalendarResponse calendarResponse) {

  public static GoogleCreateCalendarResponse of(final String calendarId, final GoogleCalendarResponse calendarResponse) {
    return new GoogleCreateCalendarResponse(calendarId, calendarResponse);
  }
}
