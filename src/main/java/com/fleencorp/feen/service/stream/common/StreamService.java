package com.fleencorp.feen.service.stream.common;

import com.fleencorp.feen.exception.stream.*;
import com.fleencorp.feen.model.domain.stream.FleenStream;
import com.fleencorp.feen.model.domain.stream.StreamAttendee;
import com.fleencorp.feen.model.dto.stream.attendance.ProcessAttendeeRequestToJoinStreamDto;
import com.fleencorp.feen.model.dto.stream.attendance.RequestToJoinStreamDto;
import com.fleencorp.feen.model.response.holder.TryToJoinPublicStreamResponse;
import com.fleencorp.feen.model.response.stream.FleenStreamResponse;
import com.fleencorp.feen.model.response.stream.attendance.RequestToJoinStreamResponse;
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
    CannotJointStreamWithoutApprovalException, AlreadyRequestedToJoinStreamException, AlreadyApprovedRequestToJoinException;

  FleenStream increaseTotalAttendeesOrGuestsAndSaveBecauseOfOrganizer(FleenStream stream);

  void decreaseTotalAttendeesOrGuestsAndSave(FleenStream stream);

  void sendJoinRequestNotificationForPrivateStream(FleenStream stream, StreamAttendee streamAttendee, FleenUser user);

  void determineUserJoinStatusForStream(List<FleenStreamResponse> responses, FleenUser user);

  void determineDifferentStatusesAndDetailsOfStreamBasedOnUser(List<FleenStreamResponse> views, FleenUser user);

  void registerAndApproveOrganizerOfStreamAsAnAttendee(FleenStream stream, FleenUser user);

  void setOtherScheduleBasedOnUserTimezone(Collection<FleenStreamResponse> responses, FleenUser user);

  void updateAttendeeRequestStatus(StreamAttendee streamAttendee, ProcessAttendeeRequestToJoinStreamDto processAttendeeRequestToJoinDto);
}
