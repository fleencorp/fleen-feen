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
  private String location;

  public static PatchCalendarEventRequest of(final String calendarId, final String eventId, final String title, final String description, final String location) {
    return PatchCalendarEventRequest.builder()
            .calendarId(calendarId)
            .eventId(eventId)
            .title(title)
            .description(description)
            .location(location)
            .build();
  }
}
