package com.fleencorp.feen.model.response.google.calendar.event;

import com.fleencorp.feen.model.response.google.calendar.event.base.GoogleCalendarEventResponse;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RetrieveCalendarEventResponse {

  private String eventId;
  private GoogleCalendarEventResponse event;
}
