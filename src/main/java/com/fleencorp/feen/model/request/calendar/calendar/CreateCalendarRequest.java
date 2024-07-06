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
public class CreateCalendarRequest {

  private String title;
  private String description;
  private String timezone;

  public static CreateCalendarRequest of(final String title, final String description, final String timezone) {
    return CreateCalendarRequest.builder()
            .title(title)
            .description(description)
            .timezone(timezone)
            .build();
  }
}
