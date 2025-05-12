package com.fleencorp.feen.service.stream.event;

import com.fleencorp.feen.model.domain.stream.FleenStream;
import com.fleencorp.feen.model.request.calendar.event.*;

public interface EventUpdateService {

  void createEventInGoogleCalendarAndAnnounceInSpace(FleenStream stream, CreateCalendarEventRequest createCalendarEventRequest);

  void createInstantEventInGoogleCalendar(FleenStream stream, CreateInstantCalendarEventRequest createInstantCalendarEventRequest);

  void updateEventInGoogleCalendar(FleenStream stream, PatchCalendarEventRequest patchCalendarEventRequest);

  void deleteEventInGoogleCalendar(DeleteCalendarEventRequest deleteCalendarEventRequest);

  void cancelEventInGoogleCalendar(CancelCalendarEventRequest cancelCalendarEventRequest);

  void rescheduleEventInGoogleCalendar(RescheduleCalendarEventRequest rescheduleCalendarEventRequest);

  void updateEventVisibility(UpdateCalendarEventVisibilityRequest updateCalendarEventVisibilityRequest);

  void notAttendingEvent(NotAttendingEventRequest notAttendingEventRequest);
}
