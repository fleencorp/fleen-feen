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
public class DeleteCalendarRequest extends CalendarRequest {

  private String calendarId;

  public static DeleteCalendarRequest of(final String calendarId, final String accessToken) {
    return DeleteCalendarRequest.builder()
      .calendarId(calendarId)
      .accessToken(accessToken)
      .build();
  }
}
