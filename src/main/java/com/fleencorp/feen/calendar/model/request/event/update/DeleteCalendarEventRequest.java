package com.fleencorp.feen.calendar.model.request.event.update;

public record DeleteCalendarEventRequest(String calendarId, String eventId) {

  public static DeleteCalendarEventRequest of(final String calendarId, final String eventId) {
    return new DeleteCalendarEventRequest(calendarId, eventId);
  }
}
