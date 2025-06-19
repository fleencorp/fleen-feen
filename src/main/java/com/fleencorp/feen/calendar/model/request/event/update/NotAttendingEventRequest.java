package com.fleencorp.feen.calendar.model.request.event.update;

public record NotAttendingEventRequest(String calendarId, String eventId, String attendeeEmailAddress) {

  public static NotAttendingEventRequest of(final String calendarId, final String eventId, final String attendeeEmailAddress) {
    return new NotAttendingEventRequest(calendarId, eventId, attendeeEmailAddress);
  }
}
