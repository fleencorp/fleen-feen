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
public class PatchCalendarRequest extends CreateCalendarRequest {

  private String calendarId;

  public static PatchCalendarRequest of(final String calendarId, final String title, final String description, final String timezone, final String accessToken) {
    return PatchCalendarRequest.builder()
            .calendarId(calendarId)
            .title(title)
            .description(description)
            .timezone(timezone)
            .accessToken(accessToken)
            .build();
  }
}
