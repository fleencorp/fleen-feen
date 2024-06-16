package com.fleencorp.feen.model.request.calendar.event;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PatchCalendarEventRequest {

  private String calendarId;
  private String eventId;
  private String title;
  private String description;
}
