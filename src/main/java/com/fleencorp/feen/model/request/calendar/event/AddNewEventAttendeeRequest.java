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
  private boolean isOrganizer;

  public static AddNewEventAttendeeRequest of(final String calendarId, final String eventId, final String attendeeEmailAddress, final String attendeeAliasOrDisplayName) {
    return new AddNewEventAttendeeRequest(calendarId, eventId, attendeeEmailAddress, attendeeAliasOrDisplayName, null, false);
  }

  public static AddNewEventAttendeeRequest of(final String calendarId, final String eventId, final String attendeeEmailAddress, final String attendeeAliasOrDisplayName, final Boolean isOrganizer) {
    return new AddNewEventAttendeeRequest(calendarId, eventId, attendeeEmailAddress, attendeeAliasOrDisplayName, null, isOrganizer);
  }

  public static AddNewEventAttendeeRequest withComment(final String calendarId, final String eventId, final String attendeeEmailAddress, final String comment) {
    return new AddNewEventAttendeeRequest(calendarId, eventId, attendeeEmailAddress, null, comment, false);
  }
}
