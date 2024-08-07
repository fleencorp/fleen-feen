package com.fleencorp.feen.model.request.calendar.event;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeleteCalendarEventRequest {

  private String calendarId;
  private String eventId;

  public static DeleteCalendarEventRequest of(final String calendarId, final String eventId) {
    return DeleteCalendarEventRequest.builder()
            .calendarId(calendarId)
            .eventId(eventId)
            .build();
  }
}
