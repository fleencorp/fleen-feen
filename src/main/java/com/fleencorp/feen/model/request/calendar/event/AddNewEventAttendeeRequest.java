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
  private String comment;

  public static AddNewEventAttendeeRequest of(final String calendarId, final String eventId, final String attendeeEmailAddress, final String attendeeAliasOrDisplayName) {
    return AddNewEventAttendeeRequest.builder()
            .calendarId(calendarId)
            .eventId(eventId)
            .attendeeEmailAddress(attendeeEmailAddress)
            .attendeeAliasOrDisplayName(attendeeAliasOrDisplayName)
            .build();
  }

  public static AddNewEventAttendeeRequest withComment(final String calendarId, final String eventId, final String attendeeEmailAddress, final String comment) {
    return AddNewEventAttendeeRequest.builder()
            .calendarId(calendarId)
            .eventId(eventId)
            .attendeeEmailAddress(attendeeEmailAddress)
            .attendeeAliasOrDisplayName(null)
            .comment(comment)
            .build();
  }
}
