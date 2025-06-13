package com.fleencorp.feen.calendar.model.request.calendar;

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
public class CreateCalendarRequest extends CalendarRequest {

  private String title;
  private String description;
  private String timezone;
  private String creatorEmailAddress;

  public static CreateCalendarRequest of(final String title, final String description, final String timezone, final String accessToken, final String creatorEmailAddress) {
    return CreateCalendarRequest.builder()
            .title(title)
            .description(description)
            .timezone(timezone)
            .accessToken(accessToken)
            .creatorEmailAddress(creatorEmailAddress)
            .build();
  }
}
