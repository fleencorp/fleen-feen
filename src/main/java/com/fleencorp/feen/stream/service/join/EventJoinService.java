package com.fleencorp.feen.stream.service.join;

import com.fleencorp.feen.calendar.exception.core.CalendarNotFoundException;
import com.fleencorp.feen.common.exception.FailedOperationException;
import com.fleencorp.feen.shared.security.RegisteredUser;
import com.fleencorp.feen.stream.exception.core.StreamAlreadyCanceledException;
import com.fleencorp.feen.stream.exception.core.StreamAlreadyHappenedException;
import com.fleencorp.feen.stream.exception.core.StreamNotCreatedByUserException;
import com.fleencorp.feen.stream.exception.core.StreamNotFoundException;
import com.fleencorp.feen.stream.model.domain.FleenStream;
import com.fleencorp.feen.stream.model.domain.StreamAttendee;
import com.fleencorp.feen.stream.model.dto.event.AddNewStreamAttendeeDto;
import com.fleencorp.feen.stream.model.response.common.AddNewStreamAttendeeResponse;

public interface EventJoinService {

  void handleJoinRequestForPrivateStreamBasedOnChatSpaceMembership(FleenStream stream, StreamAttendee streamAttendee, String comment, RegisteredUser user);

  AddNewStreamAttendeeResponse addEventAttendee(Long eventId, AddNewStreamAttendeeDto addNewStreamAttendeeDto, RegisteredUser user)
    throws StreamNotFoundException, CalendarNotFoundException, StreamNotCreatedByUserException,
      StreamAlreadyHappenedException, StreamAlreadyCanceledException, FailedOperationException;

  void addAttendeeToEventExternally(String calendarExternalId, String streamExternalId, String attendeeEmailAddress, String displayOrAliasName);
}
