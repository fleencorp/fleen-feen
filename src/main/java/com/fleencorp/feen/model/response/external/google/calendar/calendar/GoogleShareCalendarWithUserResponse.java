package com.fleencorp.feen.model.response.external.google.calendar.calendar;

import com.fleencorp.feen.model.response.external.google.calendar.calendar.base.GoogleCalendarResponse;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GoogleShareCalendarWithUserResponse {

  private String calendarId;
  private String userEmailAddress;
  private GoogleCalendarResponse calendar;

  public static GoogleShareCalendarWithUserResponse of(final String calendarId, final String userEmailAddress, final GoogleCalendarResponse calendar) {
    return GoogleShareCalendarWithUserResponse.builder()
      .calendarId(calendarId)
      .userEmailAddress(userEmailAddress)
      .calendar(calendar)
      .build();
  }
}
