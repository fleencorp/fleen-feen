package com.fleencorp.feen.stream.service.event;

import com.fleencorp.feen.calendar.model.request.event.create.AddNewEventAttendeeRequest;
import com.fleencorp.feen.calendar.model.request.event.create.CreateCalendarEventRequest;
import com.fleencorp.feen.shared.stream.contract.IsAStream;

public interface OtherEventUpdateService {

  void createEventInGoogleCalendar(IsAStream stream, CreateCalendarEventRequest createCalendarEventRequest);

  void addOrganizerOrAnyoneAsAttendeeOrGuestOfEvent(String calendarId, String eventId, String organizerEmail, String organizerDisplayName);

  void broadcastEventOrStreamCreated(IsAStream stream);

  void addNewAttendeeToCalendarEvent(AddNewEventAttendeeRequest addNewEventAttendeeRequest);
}
