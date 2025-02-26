package com.fleencorp.feen.service.stream.common;

import com.fleencorp.feen.exception.base.FailedOperationException;
import com.fleencorp.feen.exception.stream.FleenStreamNotFoundException;
import com.fleencorp.feen.exception.stream.StreamAlreadyCanceledException;
import com.fleencorp.feen.exception.stream.StreamAlreadyHappenedException;
import com.fleencorp.feen.exception.stream.StreamNotCreatedByUserException;
import com.fleencorp.feen.exception.stream.join.request.AlreadyApprovedRequestToJoinException;
import com.fleencorp.feen.exception.stream.join.request.AlreadyRequestedToJoinStreamException;
import com.fleencorp.feen.exception.stream.join.request.CannotJoinStreamWithoutApprovalException;
import com.fleencorp.feen.model.domain.stream.FleenStream;
import com.fleencorp.feen.model.domain.stream.StreamAttendee;
import com.fleencorp.feen.model.dto.stream.attendance.ProcessAttendeeRequestToJoinStreamDto;
import com.fleencorp.feen.model.dto.stream.attendance.RequestToJoinStreamDto;
import com.fleencorp.feen.model.response.holder.TryToJoinPublicStreamResponse;
import com.fleencorp.feen.model.response.holder.TryToProcessRequestToJoinStreamResponse;
import com.fleencorp.feen.model.response.stream.FleenStreamResponse;
import com.fleencorp.feen.model.response.stream.attendance.RequestToJoinStreamResponse;
import com.fleencorp.feen.model.response.stream.common.DataForRescheduleStreamResponse;
import com.fleencorp.feen.model.security.FleenUser;

import java.util.Collection;
import java.util.List;

public interface StreamService {

  FleenStream findStream(Long streamId) throws FleenStreamNotFoundException;

  RequestToJoinStreamResponse requestToJoinStream(Long streamId, RequestToJoinStreamDto requestToJoinStreamDto, FleenUser user)
    throws FleenStreamNotFoundException, StreamAlreadyCanceledException, StreamAlreadyHappenedException,
    AlreadyRequestedToJoinStreamException, AlreadyApprovedRequestToJoinException;

  TryToJoinPublicStreamResponse tryToJoinPublicStream(Long eventId, String comment, FleenUser user)
    throws FleenStreamNotFoundException, StreamAlreadyCanceledException, StreamAlreadyHappenedException,
    CannotJoinStreamWithoutApprovalException, AlreadyRequestedToJoinStreamException, AlreadyApprovedRequestToJoinException;

  TryToProcessRequestToJoinStreamResponse attemptToProcessAttendeeRequestToJoin(Long streamId, ProcessAttendeeRequestToJoinStreamDto processRequestToJoinDto, final FleenUser user)
    throws FleenStreamNotFoundException, StreamNotCreatedByUserException, StreamAlreadyHappenedException,
    StreamAlreadyCanceledException, FailedOperationException;

  DataForRescheduleStreamResponse getDataForRescheduleStream();

  void processNotAttendingStream(FleenStream stream, StreamAttendee attendee);

  void increaseTotalAttendeesOrGuestsAndSave(FleenStream stream);

  void decreaseTotalAttendeesOrGuestsAndSave(FleenStream stream);

  void sendJoinRequestNotificationForPrivateStream(FleenStream stream, StreamAttendee streamAttendee, FleenUser user);

  void determineUserJoinStatusForStream(List<FleenStreamResponse> responses, FleenUser user);

  void determineDifferentStatusesAndDetailsOfStreamBasedOnUser(List<FleenStreamResponse> views, FleenUser user);

  void registerAndApproveOrganizerOfStreamAsAnAttendee(FleenStream stream, FleenUser user);

  void setOtherScheduleBasedOnUserTimezone(Collection<FleenStreamResponse> responses, FleenUser user);

}
