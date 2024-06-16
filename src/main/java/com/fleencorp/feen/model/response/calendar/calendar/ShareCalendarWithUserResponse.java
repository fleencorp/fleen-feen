package com.fleencorp.feen.model.response.calendar.calendar;

import com.fleencorp.feen.model.response.calendar.calendar.base.GoogleCalendarResponse;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShareCalendarWithUserResponse {

  private String calendarId;
  private String userEmailAddress;
  private GoogleCalendarResponse calendar;
}
