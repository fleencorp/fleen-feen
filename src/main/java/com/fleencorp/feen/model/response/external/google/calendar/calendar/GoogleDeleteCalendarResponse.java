package com.fleencorp.feen.model.response.external.google.calendar.calendar;

import com.fleencorp.feen.model.response.external.google.calendar.calendar.base.GoogleCalendarResponse;

public record GoogleDeleteCalendarResponse(String calendarId, GoogleCalendarResponse calendarResponse) {

  public static GoogleDeleteCalendarResponse of(final String calendarId, final GoogleCalendarResponse calendarResponse) {
    return new GoogleDeleteCalendarResponse(calendarId, calendarResponse);
  }
}
