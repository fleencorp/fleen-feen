package com.fleencorp.feen.stream.service.core;

import com.fleencorp.feen.calendar.exception.core.CalendarNotFoundException;
import com.fleencorp.feen.common.exception.FailedOperationException;
import com.fleencorp.feen.oauth2.exception.core.Oauth2InvalidAuthorizationException;
import com.fleencorp.feen.stream.exception.core.StreamAlreadyCanceledException;
import com.fleencorp.feen.stream.exception.core.StreamAlreadyHappenedException;
import com.fleencorp.feen.stream.exception.core.StreamNotCreatedByUserException;
import com.fleencorp.feen.stream.exception.core.StreamNotFoundException;
import com.fleencorp.feen.stream.exception.request.AlreadyApprovedRequestToJoinException;
import com.fleencorp.feen.stream.exception.request.AlreadyRequestedToJoinStreamException;
import com.fleencorp.feen.stream.exception.request.CannotJoinPrivateStreamWithoutApprovalException;
import com.fleencorp.feen.stream.model.dto.attendee.JoinStreamDto;
import com.fleencorp.feen.stream.model.dto.attendee.NotAttendingStreamDto;
import com.fleencorp.feen.stream.model.dto.attendee.ProcessAttendeeRequestToJoinStreamDto;
import com.fleencorp.feen.stream.model.dto.attendee.RequestToJoinStreamDto;
import com.fleencorp.feen.stream.model.response.attendance.JoinStreamResponse;
import com.fleencorp.feen.stream.model.response.attendance.NotAttendingStreamResponse;
import com.fleencorp.feen.stream.model.response.attendance.ProcessAttendeeRequestToJoinStreamResponse;
import com.fleencorp.feen.stream.model.response.attendance.RequestToJoinStreamResponse;
import com.fleencorp.feen.shared.security.RegisteredUser;

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
