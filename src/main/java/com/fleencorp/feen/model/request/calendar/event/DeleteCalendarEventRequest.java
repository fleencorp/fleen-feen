package com.fleencorp.feen.model.request.calendar.event;

public record DeleteCalendarEventRequest(String calendarId, String eventId) {

  public static DeleteCalendarEventRequest of(final String calendarId, final String eventId) {
    return new DeleteCalendarEventRequest(calendarId, eventId);
  }
}
