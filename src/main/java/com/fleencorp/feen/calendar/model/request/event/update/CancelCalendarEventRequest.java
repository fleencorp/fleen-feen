package com.fleencorp.feen.calendar.model.request.event.update;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CancelCalendarEventRequest {

  private String calendarId;
  private String eventId;

  public static CancelCalendarEventRequest of(final String calendarId, final String eventId) {
    return CancelCalendarEventRequest.builder()
            .calendarId(calendarId)
            .eventId(eventId)
            .build();
  }
}
