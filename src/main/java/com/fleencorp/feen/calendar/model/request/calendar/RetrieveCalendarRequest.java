package com.fleencorp.feen.calendar.model.request.calendar;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RetrieveCalendarRequest extends CalendarRequest {

  private String calendarId;

  public static RetrieveCalendarRequest of(final String calendarId, final String accessToken) {
    final RetrieveCalendarRequest request = new RetrieveCalendarRequest();
    request.setCalendarId(calendarId);
    request.setAccessToken(accessToken);

    return request;
  }
}
