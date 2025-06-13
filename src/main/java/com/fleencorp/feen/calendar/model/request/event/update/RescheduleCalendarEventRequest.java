package com.fleencorp.feen.calendar.model.request.event.update;

import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RescheduleCalendarEventRequest {

  private String calendarId;
  private String eventId;
  private LocalDateTime startDateTime;
  private LocalDateTime endDateTime;
  private String timezone;

  public static RescheduleCalendarEventRequest of(final String calendarId, final String eventId, final LocalDateTime startDateTime, final LocalDateTime endDateTime, final String timezone) {
    return RescheduleCalendarEventRequest.builder()
            .calendarId(calendarId)
            .eventId(eventId)
            .startDateTime(startDateTime)
            .endDateTime(endDateTime)
            .timezone(timezone)
            .build();
  }
}
