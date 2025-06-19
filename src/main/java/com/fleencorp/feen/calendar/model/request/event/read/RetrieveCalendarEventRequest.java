package com.fleencorp.feen.calendar.model.request.event.read;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RetrieveCalendarEventRequest {

  private String calendarId;
  private String eventId;

  public static RetrieveCalendarEventRequest of(final String calendarId, final String eventId) {
    return RetrieveCalendarEventRequest.builder()
      .calendarId(calendarId)
      .eventId(eventId)
      .build();
  }
}
