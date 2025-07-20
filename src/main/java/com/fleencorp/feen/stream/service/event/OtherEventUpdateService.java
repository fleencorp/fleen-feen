package com.fleencorp.feen.stream.service.event;

import com.fleencorp.feen.calendar.model.request.event.create.AddNewEventAttendeeRequest;
import com.fleencorp.feen.calendar.model.request.event.create.CreateCalendarEventRequest;
import com.fleencorp.feen.stream.model.domain.FleenStream;

public interface OtherEventUpdateService {

  void createEventInGoogleCalendar(FleenStream stream, CreateCalendarEventRequest createCalendarEventRequest);

  void addOrganizerOrAnyoneAsAttendeeOrGuestOfEvent(String calendarId, String eventId, String organizerEmail, String organizerDisplayName);

  void broadcastEventOrStreamCreated(FleenStream stream);

  void addNewAttendeeToCalendarEvent(AddNewEventAttendeeRequest addNewEventAttendeeRequest);
}
