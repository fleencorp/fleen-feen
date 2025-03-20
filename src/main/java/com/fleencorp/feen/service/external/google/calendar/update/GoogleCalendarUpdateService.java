package com.fleencorp.feen.service.external.google.calendar.update;

import com.fleencorp.feen.model.request.calendar.calendar.ShareCalendarWithUserRequest;
import com.fleencorp.feen.model.response.external.google.calendar.calendar.GoogleShareCalendarWithUserResponse;

public interface GoogleCalendarUpdateService {

  GoogleShareCalendarWithUserResponse shareCalendarWithUser(ShareCalendarWithUserRequest shareCalendarWithUserRequest);
}
