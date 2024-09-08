package com.fleencorp.feen.model.request.calendar.event;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddNewEventAttendeeRequest {

  private String calendarId;
  private String eventId;
  private String attendeeEmailAddress;
  private String attendeeAliasOrDisplayName;

  public static AddNewEventAttendeeRequest of(final String calendarId, final String eventId, final String attendeeEmailAddress, final String attendeeAliasOrDisplayName) {
    return AddNewEventAttendeeRequest.builder()
            .calendarId(calendarId)
            .eventId(eventId)
            .attendeeEmailAddress(attendeeEmailAddress)
            .attendeeAliasOrDisplayName(attendeeAliasOrDisplayName)
            .build();
  }

  public static AddNewEventAttendeeRequest of(final String calendarId, final String eventId, final String attendeeEmailAddress) {
    return AddNewEventAttendeeRequest.builder()
            .calendarId(calendarId)
            .eventId(eventId)
            .attendeeEmailAddress(attendeeEmailAddress)
            .attendeeAliasOrDisplayName(null)
            .build();
  }
}
