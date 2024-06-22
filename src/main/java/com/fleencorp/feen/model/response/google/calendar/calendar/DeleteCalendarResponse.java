package com.fleencorp.feen.model.response.google.calendar.calendar;

import com.fleencorp.feen.model.response.google.calendar.calendar.base.GoogleCalendarResponse;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeleteCalendarResponse {

  private String calendarId;
  private GoogleCalendarResponse calendar;
}
