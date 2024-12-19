package com.fleencorp.feen.service.external.google.calendar.attendee;

import com.fleencorp.feen.model.request.calendar.event.AddNewEventAttendeeRequest;
import com.fleencorp.feen.model.request.calendar.event.AddNewEventAttendeesRequest;
import com.fleencorp.feen.model.request.calendar.event.NotAttendingEventRequest;
import com.fleencorp.feen.model.response.external.google.calendar.event.GoogleAddNewCalendarEventAttendeeResponse;
import com.fleencorp.feen.model.response.external.google.calendar.event.GoogleAddNewCalendarEventAttendeesResponse;
import com.fleencorp.feen.model.response.external.google.calendar.event.GoogleRetrieveCalendarEventResponse;

public interface GoogleCalendarAttendeeService {

  GoogleAddNewCalendarEventAttendeeResponse addNewAttendeeToCalendarEvent(AddNewEventAttendeeRequest addNewEventAttendeeRequest);

  GoogleAddNewCalendarEventAttendeesResponse addNewAttendeesToCalendarEvent(AddNewEventAttendeesRequest addNewEventAttendeesRequest);

  GoogleRetrieveCalendarEventResponse notAttendingEvent(NotAttendingEventRequest notAttendingEventRequest);
}
