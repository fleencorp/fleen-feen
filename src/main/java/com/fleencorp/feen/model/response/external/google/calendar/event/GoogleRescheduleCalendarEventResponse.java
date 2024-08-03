package com.fleencorp.feen.model.response.external.google.calendar.event;

import com.fleencorp.feen.model.response.external.google.calendar.event.base.GoogleCalendarEventResponse;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GoogleRescheduleCalendarEventResponse {

  private String eventId;
  private GoogleCalendarEventResponse event;

  public static GoogleRescheduleCalendarEventResponse of(final String eventId, final GoogleCalendarEventResponse event) {
    return GoogleRescheduleCalendarEventResponse.builder()
      .eventId(eventId)
      .event(event)
      .build();
  }
}
