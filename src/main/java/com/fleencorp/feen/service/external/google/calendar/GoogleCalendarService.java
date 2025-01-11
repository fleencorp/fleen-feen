package com.fleencorp.feen.service.external.google.calendar;

import com.fleencorp.feen.model.request.calendar.calendar.CreateCalendarRequest;
import com.fleencorp.feen.model.request.calendar.calendar.DeleteCalendarRequest;
import com.fleencorp.feen.model.request.calendar.calendar.PatchCalendarRequest;
import com.fleencorp.feen.model.request.calendar.calendar.RetrieveCalendarRequest;
import com.fleencorp.feen.model.response.external.google.calendar.calendar.GoogleCreateCalendarResponse;
import com.fleencorp.feen.model.response.external.google.calendar.calendar.GoogleDeleteCalendarResponse;
import com.fleencorp.feen.model.response.external.google.calendar.calendar.GooglePatchCalendarResponse;
import com.fleencorp.feen.model.response.external.google.calendar.calendar.GoogleRetrieveCalendarResponse;

public interface GoogleCalendarService {

  GoogleCreateCalendarResponse createCalendar(CreateCalendarRequest createCalendarRequest);

  GoogleRetrieveCalendarResponse retrieveCalendar(RetrieveCalendarRequest retrieveCalendarRequest);

  GoogleDeleteCalendarResponse deleteCalendar(DeleteCalendarRequest deleteCalendarRequest);

  GooglePatchCalendarResponse patchCalendar(PatchCalendarRequest patchCalendarRequest);
}
