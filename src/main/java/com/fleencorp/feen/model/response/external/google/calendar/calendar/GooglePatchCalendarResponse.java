package com.fleencorp.feen.model.response.external.google.calendar.calendar;

import com.fleencorp.feen.model.response.external.google.calendar.calendar.base.GoogleCalendarResponse;


public record GooglePatchCalendarResponse(String calendarId, GoogleCalendarResponse calendarResponse) {

  public static GooglePatchCalendarResponse of(final String calendarId, final GoogleCalendarResponse calendarResponse) {
    return new GooglePatchCalendarResponse(calendarId, calendarResponse);
  }
}
