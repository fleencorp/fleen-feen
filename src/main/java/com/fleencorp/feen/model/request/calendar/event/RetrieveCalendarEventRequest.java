package com.fleencorp.feen.model.request.calendar.event;

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
