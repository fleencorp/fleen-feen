package com.fleencorp.feen.calendar.model.request.calendar;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PatchCalendarRequest extends CreateCalendarRequest {

  private String calendarId;

  public static PatchCalendarRequest of(
      final String calendarId,
      final String title,
      final String description,
      final String timezone,
      final String accessToken) {
    final PatchCalendarRequest request = new PatchCalendarRequest();
    request.setCalendarId(calendarId);
    request.setTitle(title);
    request.setDescription(description);
    request.setTimezone(timezone);
    request.setAccessToken(accessToken);

    return request;
  }
}
