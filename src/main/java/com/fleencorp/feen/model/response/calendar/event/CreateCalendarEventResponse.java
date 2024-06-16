package com.fleencorp.feen.model.response.calendar.event;

import com.fleencorp.feen.model.response.calendar.event.base.GoogleCalendarEventResponse;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateCalendarEventResponse {

  private String eventId;
  private GoogleCalendarEventResponse event;
}
