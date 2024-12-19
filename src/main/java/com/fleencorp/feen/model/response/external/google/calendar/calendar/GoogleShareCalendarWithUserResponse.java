package com.fleencorp.feen.model.response.external.google.calendar.calendar;

import com.fleencorp.feen.model.response.external.google.calendar.calendar.base.GoogleCalendarResponse;

public record GoogleShareCalendarWithUserResponse(String calendarId, String userEmailAddress, GoogleCalendarResponse calendarResponse) {

  public static GoogleShareCalendarWithUserResponse of(final String calendarId, final String userEmailAddress, final GoogleCalendarResponse calendarResponse) {
    return new GoogleShareCalendarWithUserResponse(calendarId, userEmailAddress, calendarResponse);
  }
}
