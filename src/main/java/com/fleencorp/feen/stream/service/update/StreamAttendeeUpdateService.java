package com.fleencorp.feen.stream.service.update;

public interface StreamAttendeeUpdateService {

  void createNewEventAttendeeRequestAndSendInvitation(String calendarExternalId, String streamExternalId, String attendeeEmailAddress, String comment);
}
