package com.fleencorp.feen.model.response.external.google.calendar.calendar;

import com.fleencorp.feen.model.response.external.google.calendar.calendar.base.GoogleCalendarResponse;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GoogleDeleteCalendarResponse {

  private String calendarId;
  private GoogleCalendarResponse calendar;

  public static GoogleDeleteCalendarResponse of(final String calendarId, final GoogleCalendarResponse calendar) {
    return GoogleDeleteCalendarResponse.builder()
      .calendarId(calendarId)
      .calendar(calendar)
      .build();
  }
}
