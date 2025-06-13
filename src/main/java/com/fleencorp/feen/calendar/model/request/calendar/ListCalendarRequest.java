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
public class ListCalendarRequest extends CalendarRequest {

  private String pageToken;
  private Integer maxResultOrLimit;
  private Boolean showHidden;
  private Boolean showDeleted;
}
