package com.fleencorp.feen.service.stream.common;

import com.fleencorp.feen.exception.base.FailedOperationException;
import com.fleencorp.feen.exception.calendar.CalendarNotFoundException;
import com.fleencorp.feen.exception.google.oauth2.Oauth2InvalidAuthorizationException;
import com.fleencorp.feen.exception.stream.StreamNotFoundException;
import com.fleencorp.feen.exception.stream.core.StreamAlreadyCanceledException;
import com.fleencorp.feen.exception.stream.core.StreamAlreadyHappenedException;
import com.fleencorp.feen.exception.stream.core.StreamNotCreatedByUserException;
import com.fleencorp.feen.exception.stream.join.request.AlreadyApprovedRequestToJoinException;
import com.fleencorp.feen.exception.stream.join.request.AlreadyRequestedToJoinStreamException;
import com.fleencorp.feen.exception.stream.join.request.CannotJoinPrivateStreamWithoutApprovalException;
import com.fleencorp.feen.model.dto.stream.attendance.JoinStreamDto;
import com.fleencorp.feen.model.dto.stream.attendance.NotAttendingStreamDto;
import com.fleencorp.feen.model.dto.stream.attendance.ProcessAttendeeRequestToJoinStreamDto;
import com.fleencorp.feen.model.dto.stream.attendance.RequestToJoinStreamDto;
import com.fleencorp.feen.model.response.stream.attendance.JoinStreamResponse;
import com.fleencorp.feen.model.response.stream.attendance.NotAttendingStreamResponse;
import com.fleencorp.feen.model.response.stream.attendance.ProcessAttendeeRequestToJoinStreamResponse;
import com.fleencorp.feen.model.response.stream.attendance.RequestToJoinStreamResponse;
import com.fleencorp.feen.user.model.security.RegisteredUser;

public interface CommonStreamJoinService {

  ProcessAttendeeRequestToJoinStreamResponse processAttendeeRequestToJoinStream(Long streamId, ProcessAttendeeRequestToJoinStreamDto processRequestToJoinDto, RegisteredUser user)
    throws StreamNotFoundException, CalendarNotFoundException, Oauth2InvalidAuthorizationException,
        StreamNotCreatedByUserException, StreamAlreadyHappenedException, StreamAlreadyCanceledException, FailedOperationException;

  JoinStreamResponse joinStream(Long streamId, JoinStreamDto joinStreamDto, RegisteredUser user)
    throws StreamNotFoundException, CalendarNotFoundException, StreamAlreadyCanceledException,
      StreamAlreadyHappenedException, CannotJoinPrivateStreamWithoutApprovalException, AlreadyRequestedToJoinStreamException,
      AlreadyApprovedRequestToJoinException, FailedOperationException;

  RequestToJoinStreamResponse requestToJoinStream(Long streamId, RequestToJoinStreamDto requestToJoinStreamDto, RegisteredUser user)
    throws StreamNotFoundException, CalendarNotFoundException, StreamAlreadyCanceledException,
      StreamAlreadyHappenedException, AlreadyRequestedToJoinStreamException, AlreadyApprovedRequestToJoinException,
      FailedOperationException;

  NotAttendingStreamResponse notAttendingStream(Long streamId, NotAttendingStreamDto notAttendingStreamDto, RegisteredUser user)
    throws StreamNotFoundException, CalendarNotFoundException, StreamAlreadyCanceledException,
      StreamAlreadyHappenedException, FailedOperationException;
}
