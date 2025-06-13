package com.fleencorp.feen.service.external.google.calendar.update;

import com.fleencorp.feen.calendar.model.request.calendar.ShareCalendarWithUserRequest;
import com.fleencorp.feen.model.response.external.google.calendar.calendar.GoogleShareCalendarWithUserResponse;

public interface GoogleCalendarUpdateService {

  GoogleShareCalendarWithUserResponse shareCalendarWithUser(ShareCalendarWithUserRequest shareCalendarWithUserRequest);
}
