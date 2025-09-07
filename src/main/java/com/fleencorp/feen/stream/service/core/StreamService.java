package com.fleencorp.feen.stream.service.core;

import com.fleencorp.feen.calendar.exception.core.CalendarNotFoundException;
import com.fleencorp.feen.oauth2.exception.core.Oauth2InvalidAuthorizationException;
import com.fleencorp.feen.shared.member.contract.IsAMember;
import com.fleencorp.feen.stream.exception.core.StreamNotFoundException;
import com.fleencorp.feen.stream.model.domain.FleenStream;
import com.fleencorp.feen.stream.model.domain.StreamAttendee;
import com.fleencorp.feen.stream.model.holder.StreamOtherDetailsHolder;
import com.fleencorp.feen.stream.model.response.common.DataForRescheduleStreamResponse;

public interface StreamService {

  FleenStream findStream(Long streamId) throws StreamNotFoundException;

  DataForRescheduleStreamResponse getDataForRescheduleStream();

  void verifyStreamDetailAllDetails(FleenStream stream, Long userId);

  void processNotAttendingStream(FleenStream stream, StreamAttendee attendee);

  void increaseTotalAttendeesOrGuests(FleenStream stream);

  void validateStreamAndUserForProtectedStream(FleenStream stream, Long userId);

  void registerAndApproveOrganizerOfStreamAsAnAttendee(FleenStream stream, IsAMember member);

  StreamOtherDetailsHolder retrieveStreamOtherDetailsHolder(FleenStream stream, IsAMember user) throws CalendarNotFoundException, Oauth2InvalidAuthorizationException;

  boolean existsByAttendees(IsAMember viewer, IsAMember target);
}
