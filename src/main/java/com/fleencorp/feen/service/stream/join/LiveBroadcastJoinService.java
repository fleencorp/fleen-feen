package com.fleencorp.feen.service.stream.join;

import com.fleencorp.feen.exception.base.FailedOperationException;
import com.fleencorp.feen.exception.stream.*;
import com.fleencorp.feen.model.dto.stream.attendance.JoinStreamDto;
import com.fleencorp.feen.model.dto.stream.attendance.ProcessAttendeeRequestToJoinStreamDto;
import com.fleencorp.feen.model.dto.stream.attendance.RequestToJoinStreamDto;
import com.fleencorp.feen.model.response.stream.attendance.JoinStreamResponse;
import com.fleencorp.feen.model.response.stream.attendance.NotAttendingStreamResponse;
import com.fleencorp.feen.model.response.stream.attendance.ProcessAttendeeRequestToJoinStreamResponse;
import com.fleencorp.feen.model.response.stream.attendance.RequestToJoinStreamResponse;
import com.fleencorp.feen.model.security.FleenUser;

public interface LiveBroadcastJoinService {

  NotAttendingStreamResponse notAttendingLiveBroadcast(Long liveBroadcastId, FleenUser user)
    throws FleenStreamNotFoundException, FailedOperationException;

  JoinStreamResponse joinLiveBroadcast(Long liveBroadcastId, JoinStreamDto joinStreamDto, FleenUser user)
    throws FleenStreamNotFoundException, StreamAlreadyCanceledException, StreamAlreadyHappenedException,
    CannotJointStreamWithoutApprovalException, AlreadyRequestedToJoinStreamException, AlreadyApprovedRequestToJoinException;

  RequestToJoinStreamResponse requestToJoinLiveBroadcast(Long eventId, RequestToJoinStreamDto requestToJoinStreamDto, FleenUser user)
    throws FleenStreamNotFoundException, StreamAlreadyCanceledException, StreamAlreadyHappenedException,
    AlreadyRequestedToJoinStreamException, AlreadyApprovedRequestToJoinException;

  ProcessAttendeeRequestToJoinStreamResponse processAttendeeRequestToJoinLiveBroadcast(Long liveBroadcastId, ProcessAttendeeRequestToJoinStreamDto processAttendeeRequestToJoinStreamDto, FleenUser user)
    throws FleenStreamNotFoundException, StreamNotCreatedByUserException, StreamAlreadyHappenedException,
    StreamAlreadyCanceledException, FailedOperationException;
}
