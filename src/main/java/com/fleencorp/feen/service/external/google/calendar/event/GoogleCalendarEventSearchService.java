package com.fleencorp.feen.service.external.google.calendar.event;

import com.fleencorp.feen.model.request.calendar.event.ListCalendarEventRequest;
import com.fleencorp.feen.model.request.calendar.event.RetrieveCalendarEventRequest;
import com.fleencorp.feen.model.response.external.google.calendar.event.GoogleListCalendarEventResponse;
import com.fleencorp.feen.model.response.external.google.calendar.event.GoogleRetrieveCalendarEventResponse;

public interface GoogleCalendarEventSearchService {

  GoogleListCalendarEventResponse listEvent(ListCalendarEventRequest listCalendarEventRequest);

  GoogleRetrieveCalendarEventResponse retrieveEvent(RetrieveCalendarEventRequest retrieveCalendarEventRequest);
}
