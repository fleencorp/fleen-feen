package com.fleencorp.feen.model.request.calendar.calendar;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ListCalendarRequest {

  private String pageToken;
  private Integer maxResultOrLimit;
  private Boolean showHidden;
  private Boolean showDeleted;
}
