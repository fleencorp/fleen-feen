package com.fleencorp.feen.service.stream.update;

public interface AttendeeUpdateService {

  void createNewEventAttendeeRequestAndSendInvitation(String calendarExternalId, String streamExternalId, String attendeeEmailAddress, String comment);
}
