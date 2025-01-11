package com.fleencorp.feen.service.external.google.calendar.update;

import com.fleencorp.feen.model.request.calendar.calendar.ShareCalendarWithUserRequest;
import com.fleencorp.feen.model.response.external.google.calendar.calendar.GoogleShareCalendarWithUserResponse;

public interface GoogleCalendarUpdateService {

  void shareCalendarWithServiceAccountEmail(String calendarId, String calendarCreatorEmailAddress, String accessToken);

  GoogleShareCalendarWithUserResponse shareCalendarWithUser(ShareCalendarWithUserRequest shareCalendarWithUserRequest);
}
