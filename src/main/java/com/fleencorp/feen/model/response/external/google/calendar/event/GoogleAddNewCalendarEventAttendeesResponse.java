package com.fleencorp.feen.model.response.external.google.calendar.event;

import com.fleencorp.feen.model.response.external.google.calendar.event.base.GoogleCalendarEventResponse;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GoogleAddNewCalendarEventAttendeesResponse {

  private String eventId;
  private GoogleCalendarEventResponse event;

  public static GoogleAddNewCalendarEventAttendeesResponse of(final String eventId, final GoogleCalendarEventResponse event) {
    return GoogleAddNewCalendarEventAttendeesResponse.builder()
      .eventId(eventId)
      .event(event)
      .build();
  }
}
