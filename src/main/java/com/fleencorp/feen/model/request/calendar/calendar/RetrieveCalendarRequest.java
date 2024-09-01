package com.fleencorp.feen.model.request.calendar.calendar;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RetrieveCalendarRequest extends CalendarRequest {

  private String calendarId;

  public static RetrieveCalendarRequest of(final String calendarId, final String accessToken) {
    return RetrieveCalendarRequest.builder()
      .calendarId(calendarId)
      .accessToken(accessToken)
      .build();
  }
}
