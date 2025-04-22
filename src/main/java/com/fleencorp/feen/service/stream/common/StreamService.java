package com.fleencorp.feen.service.stream.common;

import com.fleencorp.feen.exception.calendar.CalendarNotFoundException;
import com.fleencorp.feen.exception.google.oauth2.Oauth2InvalidAuthorizationException;
import com.fleencorp.feen.exception.stream.FleenStreamNotFoundException;
import com.fleencorp.feen.model.domain.stream.FleenStream;
import com.fleencorp.feen.model.domain.stream.StreamAttendee;
import com.fleencorp.feen.model.holder.StreamOtherDetailsHolder;
import com.fleencorp.feen.model.response.stream.FleenStreamResponse;
import com.fleencorp.feen.model.response.stream.common.DataForRescheduleStreamResponse;
import com.fleencorp.feen.model.security.FleenUser;

import java.util.Collection;
import java.util.List;

public interface StreamService {

  FleenStream findStream(Long streamId) throws FleenStreamNotFoundException;

  DataForRescheduleStreamResponse getDataForRescheduleStream();

  void verifyStreamDetailAllDetails(FleenStream stream, FleenUser user);

  void processNotAttendingStream(FleenStream stream, StreamAttendee attendee);

  void increaseTotalAttendeesOrGuests(FleenStream stream);

  void decreaseTotalAttendeesOrGuests(FleenStream stream);

  void validateStreamAndUserForProtectedStream(FleenStream stream, FleenUser user);

  void determineUserJoinStatusForStream(List<FleenStreamResponse> responses, FleenUser user);

  void determineDifferentStatusesAndDetailsOfStreamBasedOnUser(List<FleenStreamResponse> views, FleenUser user);

  void registerAndApproveOrganizerOfStreamAsAnAttendee(FleenStream stream, FleenUser user);

  void setOtherScheduleBasedOnUserTimezone(Collection<FleenStreamResponse> responses, FleenUser user);

  StreamOtherDetailsHolder retrieveStreamOtherDetailsHolder(FleenStream stream, FleenUser user) throws CalendarNotFoundException, Oauth2InvalidAuthorizationException;
}
