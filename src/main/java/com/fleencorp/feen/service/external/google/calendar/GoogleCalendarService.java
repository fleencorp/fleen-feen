package com.fleencorp.feen.service.external.google.calendar;

import com.fleencorp.feen.model.request.calendar.calendar.*;
import com.fleencorp.feen.model.response.external.google.calendar.calendar.*;

public interface GoogleCalendarService {

  GoogleCreateCalendarResponse createCalendar(CreateCalendarRequest createCalendarRequest);

  GoogleRetrieveCalendarResponse retrieveCalendar(RetrieveCalendarRequest retrieveCalendarRequest);

  GoogleDeleteCalendarResponse deleteCalendar(DeleteCalendarRequest deleteCalendarRequest);

  GooglePatchCalendarResponse patchCalendar(PatchCalendarRequest patchCalendarRequest);

  GoogleShareCalendarWithUserResponse shareCalendarWithUser(ShareCalendarWithUserRequest shareCalendarWithUserRequest);
}
