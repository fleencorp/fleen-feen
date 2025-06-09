package com.fleencorp.feen.service.stream.join;

import com.fleencorp.feen.exception.base.FailedOperationException;
import com.fleencorp.feen.exception.calendar.CalendarNotFoundException;
import com.fleencorp.feen.exception.stream.StreamNotFoundException;
import com.fleencorp.feen.exception.stream.core.StreamAlreadyCanceledException;
import com.fleencorp.feen.exception.stream.core.StreamAlreadyHappenedException;
import com.fleencorp.feen.exception.stream.core.StreamNotCreatedByUserException;
import com.fleencorp.feen.model.domain.stream.FleenStream;
import com.fleencorp.feen.model.domain.stream.StreamAttendee;
import com.fleencorp.feen.model.dto.event.AddNewStreamAttendeeDto;
import com.fleencorp.feen.model.response.stream.common.AddNewStreamAttendeeResponse;
import com.fleencorp.feen.user.model.security.RegisteredUser;

public interface EventJoinService {

  void handleJoinRequestForPrivateStreamBasedOnChatSpaceMembership(FleenStream stream, StreamAttendee streamAttendee, String comment, RegisteredUser user);

  AddNewStreamAttendeeResponse addEventAttendee(Long eventId, AddNewStreamAttendeeDto addNewStreamAttendeeDto, RegisteredUser user)
    throws StreamNotFoundException, CalendarNotFoundException, StreamNotCreatedByUserException,
    StreamAlreadyHappenedException, StreamAlreadyCanceledException, FailedOperationException;

  void addAttendeeToEventExternally(String calendarExternalId, String streamExternalId, String attendeeEmailAddress, String displayOrAliasName);
}
