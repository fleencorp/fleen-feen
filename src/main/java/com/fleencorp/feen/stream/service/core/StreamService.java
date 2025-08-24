package com.fleencorp.feen.stream.service.core;

import com.fleencorp.feen.calendar.exception.core.CalendarNotFoundException;
import com.fleencorp.feen.oauth2.exception.core.Oauth2InvalidAuthorizationException;
import com.fleencorp.feen.stream.exception.core.StreamNotFoundException;
import com.fleencorp.feen.stream.model.domain.FleenStream;
import com.fleencorp.feen.stream.model.domain.StreamAttendee;
import com.fleencorp.feen.stream.model.holder.StreamOtherDetailsHolder;
import com.fleencorp.feen.stream.model.response.common.DataForRescheduleStreamResponse;
import com.fleencorp.feen.user.model.domain.Member;
import com.fleencorp.feen.user.model.security.RegisteredUser;

public interface StreamService {

  FleenStream findStream(Long streamId) throws StreamNotFoundException;

  DataForRescheduleStreamResponse getDataForRescheduleStream();

  void verifyStreamDetailAllDetails(FleenStream stream, RegisteredUser user);

  void processNotAttendingStream(FleenStream stream, StreamAttendee attendee);

  void increaseTotalAttendeesOrGuests(FleenStream stream);

  void validateStreamAndUserForProtectedStream(FleenStream stream, RegisteredUser user);

  void registerAndApproveOrganizerOfStreamAsAnAttendee(FleenStream stream, RegisteredUser user);

  StreamOtherDetailsHolder retrieveStreamOtherDetailsHolder(FleenStream stream, RegisteredUser user) throws CalendarNotFoundException, Oauth2InvalidAuthorizationException;

  boolean existsByAttendees(Member viewer, Member target);
}
