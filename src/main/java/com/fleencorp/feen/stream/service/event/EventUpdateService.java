package com.fleencorp.feen.stream.service.event;

import com.fleencorp.feen.calendar.model.request.event.create.CreateCalendarEventRequest;
import com.fleencorp.feen.calendar.model.request.event.create.CreateInstantCalendarEventRequest;
import com.fleencorp.feen.calendar.model.request.event.update.*;
import com.fleencorp.feen.shared.stream.contract.IsAStream;
import com.fleencorp.feen.stream.model.domain.FleenStream;

public interface EventUpdateService {

  void createEventInGoogleCalendarAndAnnounceInSpace(IsAStream stream, CreateCalendarEventRequest createCalendarEventRequest);

  void createInstantEventInGoogleCalendar(IsAStream stream, CreateInstantCalendarEventRequest createInstantCalendarEventRequest);

  void updateEventInGoogleCalendar(IsAStream stream, PatchCalendarEventRequest patchCalendarEventRequest);

  void deleteEventInGoogleCalendar(DeleteCalendarEventRequest deleteCalendarEventRequest);

  void cancelEventInGoogleCalendar(CancelCalendarEventRequest cancelCalendarEventRequest);

  void rescheduleEventInGoogleCalendar(RescheduleCalendarEventRequest rescheduleCalendarEventRequest);

  void updateEventVisibility(UpdateCalendarEventVisibilityRequest updateCalendarEventVisibilityRequest);

  void notAttendingEvent(NotAttendingEventRequest notAttendingEventRequest);
}
