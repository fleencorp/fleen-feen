package com.fleencorp.feen.model.response.external.google.calendar.calendar;

import com.fleencorp.feen.model.response.external.google.calendar.calendar.base.GoogleCalendarResponse;

import java.util.List;

import static java.util.Objects.isNull;

public record GoogleListCalendarResponse(List<GoogleCalendarResponse> calendarResponses) {

  public static GoogleListCalendarResponse of(final List<GoogleCalendarResponse> calendarResponses) {
    return new GoogleListCalendarResponse(isNull(calendarResponses) ? List.of() : calendarResponses);
  }

  public static GoogleListCalendarResponse of() {
    return new GoogleListCalendarResponse(List.of());
  }
}
