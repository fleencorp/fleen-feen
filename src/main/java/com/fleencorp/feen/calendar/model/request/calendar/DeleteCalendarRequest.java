package com.fleencorp.feen.calendar.model.request.calendar;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeleteCalendarRequest extends CalendarRequest {

  private String calendarId;

  public static DeleteCalendarRequest of(final String calendarId, final String accessToken) {
    final DeleteCalendarRequest request = new DeleteCalendarRequest();
    request.setCalendarId(calendarId);
    request.setAccessToken(accessToken);

    return request;
  }
}
