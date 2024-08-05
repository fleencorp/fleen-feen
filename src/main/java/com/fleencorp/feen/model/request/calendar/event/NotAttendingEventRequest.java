package com.fleencorp.feen.model.request.calendar.event;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotAttendingEventRequest {

  private String calendarId;
  private String eventId;
  private String attendeeEmailAddress;

  public static NotAttendingEventRequest of(String calendarId, String eventId, String attendeeEmailAddress) {
    return NotAttendingEventRequest.builder()
      .calendarId(calendarId)
      .eventId(eventId)
      .attendeeEmailAddress(attendeeEmailAddress)
      .build();
  }
}
