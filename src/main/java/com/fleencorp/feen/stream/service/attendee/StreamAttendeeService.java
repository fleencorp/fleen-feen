package com.fleencorp.feen.stream.service.attendee;

import com.fleencorp.feen.chat.space.model.search.core.RequestToJoinSearchResult;
import com.fleencorp.feen.common.exception.FailedOperationException;
import com.fleencorp.feen.stream.exception.core.StreamNotFoundException;
import com.fleencorp.feen.stream.model.domain.FleenStream;
import com.fleencorp.feen.stream.model.domain.StreamAttendee;
import com.fleencorp.feen.stream.model.request.search.StreamAttendeeSearchRequest;
import com.fleencorp.feen.stream.model.response.StreamResponse;
import com.fleencorp.feen.stream.model.response.attendee.StreamAttendeeResponse;
import com.fleencorp.feen.stream.model.search.attendee.StreamAttendeeSearchResult;
import com.fleencorp.feen.shared.security.RegisteredUser;

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
