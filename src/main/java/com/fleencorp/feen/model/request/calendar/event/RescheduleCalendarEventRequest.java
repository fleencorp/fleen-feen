package com.fleencorp.feen.model.request.calendar.event;

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
}
