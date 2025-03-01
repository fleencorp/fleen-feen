package com.fleencorp.feen.service.stream.common;

import com.fleencorp.feen.exception.base.FailedOperationException;
import com.fleencorp.feen.exception.calendar.CalendarNotFoundException;
import com.fleencorp.feen.exception.google.oauth2.Oauth2InvalidAuthorizationException;
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

public interface CommonStreamJoinService {

  ProcessAttendeeRequestToJoinStreamResponse processAttendeeRequestToJoinStream(Long streamId, ProcessAttendeeRequestToJoinStreamDto processRequestToJoinDto, FleenUser user)
    throws FleenStreamNotFoundException, CalendarNotFoundException, Oauth2InvalidAuthorizationException,
        StreamNotCreatedByUserException, StreamAlreadyHappenedException, StreamAlreadyCanceledException, FailedOperationException;

  JoinStreamResponse joinStream(Long streamId, JoinStreamDto joinStreamDto, FleenUser user)
    throws FleenStreamNotFoundException, CalendarNotFoundException, StreamAlreadyCanceledException,
      StreamAlreadyHappenedException, CannotJoinStreamWithoutApprovalException, AlreadyRequestedToJoinStreamException,
      AlreadyApprovedRequestToJoinException, FailedOperationException;

  RequestToJoinStreamResponse requestToJoinStream(Long streamId, RequestToJoinStreamDto requestToJoinStreamDto, FleenUser user)
    throws FleenStreamNotFoundException, CalendarNotFoundException, StreamAlreadyCanceledException,
      StreamAlreadyHappenedException, AlreadyRequestedToJoinStreamException, AlreadyApprovedRequestToJoinException,
      FailedOperationException;

  NotAttendingStreamResponse notAttendingStream(Long streamId, NotAttendingStreamDto notAttendingStreamDto, FleenUser user)
    throws FleenStreamNotFoundException, CalendarNotFoundException, StreamAlreadyCanceledException,
      StreamAlreadyHappenedException, FailedOperationException;
}
