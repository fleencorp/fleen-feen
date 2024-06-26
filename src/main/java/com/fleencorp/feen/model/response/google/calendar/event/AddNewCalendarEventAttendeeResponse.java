package com.fleencorp.feen.model.response.google.calendar.event;

import com.fleencorp.feen.model.response.google.calendar.event.base.GoogleCalendarEventResponse;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddNewCalendarEventAttendeeResponse {

  private String eventId;
  private String userEmailAddress;
  private GoogleCalendarEventResponse event;
}
