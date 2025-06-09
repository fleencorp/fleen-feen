package com.fleencorp.feen.service.stream.attendee;

import com.fleencorp.feen.exception.base.FailedOperationException;
import com.fleencorp.feen.exception.stream.StreamNotFoundException;
import com.fleencorp.feen.model.domain.stream.FleenStream;
import com.fleencorp.feen.model.domain.stream.StreamAttendee;
import com.fleencorp.feen.model.request.search.stream.StreamAttendeeSearchRequest;
import com.fleencorp.feen.model.response.stream.StreamResponse;
import com.fleencorp.feen.model.response.stream.attendee.StreamAttendeeResponse;
import com.fleencorp.feen.model.search.join.RequestToJoinSearchResult;
import com.fleencorp.feen.model.search.stream.attendee.StreamAttendeeSearchResult;
import com.fleencorp.feen.user.security.RegisteredUser;

import java.util.Collection;
import java.util.Optional;

public interface StreamAttendeeService {

  StreamAttendeeSearchResult findStreamAttendees(Long streamId, StreamAttendeeSearchRequest searchRequest);

  void checkIfAttendeeIsMemberOfChatSpaceAndSendInvitationForJoinStreamRequest(boolean isAttendeeMemberOfChatSpace, String streamExternalId, String comment, RegisteredUser user);

  Collection<StreamAttendeeResponse> getAttendeesGoingToStream(StreamResponse streamResponse);

  StreamAttendeeSearchResult getStreamAttendees(Long streamId, StreamAttendeeSearchRequest searchRequest) throws StreamNotFoundException;

  Optional<StreamAttendee> findAttendeeByMemberId(FleenStream stream, Long userId);

  Optional<StreamAttendee> findAttendee(FleenStream stream, Long attendeeId);

  RequestToJoinSearchResult getAttendeeRequestsToJoinStream(Long streamId, StreamAttendeeSearchRequest searchRequest, RegisteredUser user);

  StreamAttendee getExistingOrCreateNewStreamAttendee(FleenStream stream, String comment, RegisteredUser user) throws FailedOperationException;
}
