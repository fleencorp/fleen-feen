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
import com.fleencorp.feen.model.security.FleenUser;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface StreamAttendeeService {

  StreamAttendeeSearchResult findStreamAttendees(Long streamId, StreamAttendeeSearchRequest searchRequest);

  Set<StreamAttendeeResponse> toStreamAttendeeResponsesSet(StreamResponse streamResponse, Collection<StreamAttendee> streamAttendees);

  void setFirst10AttendeesAttendingInAnyOrderOnStreams(List<StreamResponse> streams);

  void setStreamAttendeesAndTotalAttendeesAttending(List<StreamResponse> streams);

  void checkIfAttendeeIsMemberOfChatSpaceAndSendInvitationForJoinStreamRequest(boolean isAttendeeMemberOfChatSpace, String streamExternalId, String comment, FleenUser user);

  Set<StreamAttendee> getAttendeesGoingToStream(FleenStream stream);

  StreamAttendeeSearchResult getStreamAttendees(Long streamId, StreamAttendeeSearchRequest searchRequest) throws StreamNotFoundException;

  Optional<StreamAttendee> findAttendeeByMemberId(FleenStream stream, Long userId);

  Optional<StreamAttendee> findAttendee(FleenStream stream, Long attendeeId);

  RequestToJoinSearchResult getAttendeeRequestsToJoinStream(Long streamId, StreamAttendeeSearchRequest searchRequest, FleenUser user);

  StreamAttendee getExistingOrCreateNewStreamAttendee(FleenStream stream, String comment, FleenUser user) throws FailedOperationException;
}
