package com.fleencorp.feen.model.request.calendar.calendar;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RetrieveCalendarRequest {

  private String calendarId;
}
