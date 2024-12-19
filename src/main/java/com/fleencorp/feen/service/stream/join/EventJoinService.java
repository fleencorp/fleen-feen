package com.fleencorp.feen.service.stream.join;

import com.fleencorp.feen.exception.base.FailedOperationException;
import com.fleencorp.feen.exception.calendar.CalendarNotFoundException;
import com.fleencorp.feen.exception.stream.*;
import com.fleencorp.feen.model.dto.event.AddNewStreamAttendeeDto;
import com.fleencorp.feen.model.dto.stream.attendance.JoinStreamDto;
import com.fleencorp.feen.model.dto.stream.attendance.NotAttendingStreamDto;
import com.fleencorp.feen.model.dto.stream.attendance.ProcessAttendeeRequestToJoinStreamDto;
import com.fleencorp.feen.model.dto.stream.attendance.RequestToJoinStreamDto;
import com.fleencorp.feen.model.response.stream.attendance.JoinStreamResponse;
import com.fleencorp.feen.model.response.stream.attendance.NotAttendingStreamResponse;
import com.fleencorp.feen.model.response.stream.attendance.ProcessAttendeeRequestToJoinStreamResponse;
import com.fleencorp.feen.model.response.stream.attendance.RequestToJoinStreamResponse;
import com.fleencorp.feen.model.response.stream.common.AddNewStreamAttendeeResponse;
import com.fleencorp.feen.model.security.FleenUser;

public interface EventJoinService {

  NotAttendingStreamResponse notAttendingEvent(Long eventId, NotAttendingStreamDto notAttendingStreamDto, FleenUser user)
    throws FleenStreamNotFoundException, CalendarNotFoundException, FailedOperationException;

  JoinStreamResponse joinEvent(Long eventId, JoinStreamDto joinStreamDto, FleenUser user)
    throws CalendarNotFoundException, FleenStreamNotFoundException, StreamAlreadyCanceledException,
    StreamAlreadyHappenedException, CannotJointStreamWithoutApprovalException, AlreadyRequestedToJoinStreamException,
    AlreadyApprovedRequestToJoinException;

  RequestToJoinStreamResponse requestToJoinEvent(Long eventId, RequestToJoinStreamDto requestToJoinStreamDto, FleenUser user)
    throws FleenStreamNotFoundException, StreamAlreadyCanceledException, StreamAlreadyHappenedException,
    AlreadyRequestedToJoinStreamException, AlreadyApprovedRequestToJoinException;

  ProcessAttendeeRequestToJoinStreamResponse processAttendeeRequestToJoinEvent(Long eventId, ProcessAttendeeRequestToJoinStreamDto processAttendeeRequestToJoinStreamDto, FleenUser user)
    throws FleenStreamNotFoundException, StreamNotCreatedByUserException, StreamAlreadyHappenedException,
    StreamAlreadyCanceledException, FailedOperationException;

  AddNewStreamAttendeeResponse addEventAttendee(Long eventId, AddNewStreamAttendeeDto addNewStreamAttendeeDto, FleenUser user)
    throws CalendarNotFoundException, FleenStreamNotFoundException, StreamNotCreatedByUserException,
    StreamAlreadyHappenedException, StreamAlreadyCanceledException, FailedOperationException;
}
