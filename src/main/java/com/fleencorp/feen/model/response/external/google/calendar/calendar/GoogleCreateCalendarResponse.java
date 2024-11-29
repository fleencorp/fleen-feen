package com.fleencorp.feen.model.response.external.google.calendar.calendar;

import com.fleencorp.feen.model.response.external.google.calendar.calendar.base.GoogleCalendarResponse;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GoogleCreateCalendarResponse {

  private String calendarId;
  private GoogleCalendarResponse calendar;

  public static GoogleCreateCalendarResponse of(final String calendarId, final GoogleCalendarResponse calendar) {
    return GoogleCreateCalendarResponse.builder()
      .calendarId(calendarId)
      .calendar(calendar)
      .build();
  }
}
