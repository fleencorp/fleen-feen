package com.fleencorp.feen.model.response.external.google.calendar.calendar;

import com.fleencorp.feen.model.response.external.google.calendar.calendar.base.GoogleCalendarResponse;

public record GoogleRetrieveCalendarResponse(String calendarId, GoogleCalendarResponse calendarResponse) {

  public static GoogleRetrieveCalendarResponse of(final String calendarId, final GoogleCalendarResponse calendarResponse) {
    return new GoogleRetrieveCalendarResponse(calendarId, calendarResponse);
  }
}
