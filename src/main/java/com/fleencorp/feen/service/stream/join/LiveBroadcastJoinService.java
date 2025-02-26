package com.fleencorp.feen.service.stream.join;

import com.fleencorp.feen.exception.base.FailedOperationException;
import com.fleencorp.feen.exception.stream.FleenStreamNotFoundException;
import com.fleencorp.feen.exception.stream.StreamAlreadyCanceledException;
import com.fleencorp.feen.exception.stream.StreamAlreadyHappenedException;
import com.fleencorp.feen.exception.stream.StreamNotCreatedByUserException;
import com.fleencorp.feen.exception.stream.join.request.AlreadyApprovedRequestToJoinException;
import com.fleencorp.feen.exception.stream.join.request.AlreadyRequestedToJoinStreamException;
import com.fleencorp.feen.exception.stream.join.request.CannotJoinStreamWithoutApprovalException;
import com.fleencorp.feen.model.dto.stream.attendance.JoinStreamDto;
import com.fleencorp.feen.model.dto.stream.attendance.NotAttendingStreamDto;
import com.fleencorp.feen.model.dto.stream.attendance.ProcessAttendeeRequestToJoinStreamDto;
import com.fleencorp.feen.model.dto.stream.attendance.RequestToJoinStreamDto;
import com.fleencorp.feen.model.response.stream.attendance.JoinStreamResponse;
import com.fleencorp.feen.model.response.stream.attendance.NotAttendingStreamResponse;
import com.fleencorp.feen.model.response.stream.attendance.ProcessAttendeeRequestToJoinStreamResponse;
import com.fleencorp.feen.model.response.stream.attendance.RequestToJoinStreamResponse;
import com.fleencorp.feen.model.security.FleenUser;

public interface LiveBroadcastJoinService {

  NotAttendingStreamResponse notAttendingLiveBroadcast(Long liveBroadcastId, NotAttendingStreamDto notAttendingStreamDto, FleenUser user)
    throws FleenStreamNotFoundException, FailedOperationException;

  JoinStreamResponse joinLiveBroadcast(Long liveBroadcastId, JoinStreamDto joinStreamDto, FleenUser user)
    throws FleenStreamNotFoundException, StreamAlreadyCanceledException, StreamAlreadyHappenedException,
    CannotJoinStreamWithoutApprovalException, AlreadyRequestedToJoinStreamException, AlreadyApprovedRequestToJoinException;

  RequestToJoinStreamResponse requestToJoinLiveBroadcast(Long streamId, RequestToJoinStreamDto requestToJoinStreamDto, FleenUser user)
    throws FleenStreamNotFoundException, StreamAlreadyCanceledException, StreamAlreadyHappenedException,
    AlreadyRequestedToJoinStreamException, AlreadyApprovedRequestToJoinException;

  ProcessAttendeeRequestToJoinStreamResponse processAttendeeRequestToJoinLiveBroadcast(Long liveBroadcastId, ProcessAttendeeRequestToJoinStreamDto processAttendeeRequestToJoinStreamDto, FleenUser user)
    throws FleenStreamNotFoundException, StreamNotCreatedByUserException, StreamAlreadyHappenedException,
    StreamAlreadyCanceledException, FailedOperationException;
}
