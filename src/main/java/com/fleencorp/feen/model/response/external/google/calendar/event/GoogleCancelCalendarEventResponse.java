package com.fleencorp.feen.model.response.external.google.calendar.event;

import com.fleencorp.feen.model.response.external.google.calendar.event.base.GoogleCalendarEventResponse;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GoogleCancelCalendarEventResponse {

  private String eventId;
  private GoogleCalendarEventResponse event;

  public static GoogleCancelCalendarEventResponse of(final String eventId, final GoogleCalendarEventResponse event) {
    return GoogleCancelCalendarEventResponse.builder()
      .eventId(eventId)
      .event(event)
      .build();
  }
}
