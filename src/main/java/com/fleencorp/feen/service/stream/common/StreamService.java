package com.fleencorp.feen.service.stream.common;

import com.fleencorp.feen.calendar.exception.core.CalendarNotFoundException;
import com.fleencorp.feen.exception.stream.StreamNotFoundException;
import com.fleencorp.feen.model.domain.stream.FleenStream;
import com.fleencorp.feen.model.domain.stream.StreamAttendee;
import com.fleencorp.feen.model.holder.StreamOtherDetailsHolder;
import com.fleencorp.feen.model.response.stream.common.DataForRescheduleStreamResponse;
import com.fleencorp.feen.oauth2.exception.core.Oauth2InvalidAuthorizationException;
import com.fleencorp.feen.user.model.domain.Member;
import com.fleencorp.feen.user.model.security.RegisteredUser;

public interface StreamService {

  FleenStream findStream(Long streamId) throws StreamNotFoundException;

  DataForRescheduleStreamResponse getDataForRescheduleStream();

  void verifyStreamDetailAllDetails(FleenStream stream, RegisteredUser user);

  void processNotAttendingStream(FleenStream stream, StreamAttendee attendee);

  void increaseTotalAttendeesOrGuests(FleenStream stream);

  void decreaseTotalAttendeesOrGuests(FleenStream stream);

  void validateStreamAndUserForProtectedStream(FleenStream stream, RegisteredUser user);

  void registerAndApproveOrganizerOfStreamAsAnAttendee(FleenStream stream, RegisteredUser user);

  StreamOtherDetailsHolder retrieveStreamOtherDetailsHolder(FleenStream stream, RegisteredUser user) throws CalendarNotFoundException, Oauth2InvalidAuthorizationException;

  boolean existsByAttendees(Member viewer, Member target);

  Long incrementLikeCount(Long streamId);

  Long decrementLikeCount(Long streamId);
}
