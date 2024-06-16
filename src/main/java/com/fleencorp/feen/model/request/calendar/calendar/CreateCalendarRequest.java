package com.fleencorp.feen.model.request.calendar.calendar;

import lombok.*;
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
}
