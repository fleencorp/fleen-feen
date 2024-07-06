package com.fleencorp.feen.model.response.external.google.calendar.event;

import com.fleencorp.feen.model.response.external.google.calendar.event.base.GoogleCalendarEventResponse;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GoogleAddNewCalendarEventAttendeeResponse {

  private String eventId;
  private String userEmailAddress;
  private GoogleCalendarEventResponse event;
}
