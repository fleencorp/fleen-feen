package com.fleencorp.feen.model.response.external.google.calendar.event;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fleencorp.feen.model.response.external.google.calendar.event.base.GoogleCalendarEventResponse;
import com.google.api.services.calendar.model.Event;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GoogleRetrieveCalendarEventResponse {

  private String eventId;
  private GoogleCalendarEventResponse event;

  @JsonIgnore
  private Event calendarEvent;

  public static GoogleRetrieveCalendarEventResponse of(final String eventId, final Event event, final GoogleCalendarEventResponse calendarEventResponse) {
    return GoogleRetrieveCalendarEventResponse.builder()
      .eventId(eventId)
      .calendarEvent(event)
      .event(calendarEventResponse)
      .build();
  }
}
