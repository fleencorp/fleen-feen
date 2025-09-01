package com.fleencorp.feen.stream.service.attendee;

import com.fleencorp.feen.chat.space.model.search.core.RequestToJoinSearchResult;
import com.fleencorp.feen.common.exception.FailedOperationException;
import com.fleencorp.feen.stream.constant.attendee.StreamAttendeeRequestToJoinStatus;
import com.fleencorp.feen.stream.constant.core.StreamType;
import com.fleencorp.feen.stream.exception.core.StreamNotFoundException;
import com.fleencorp.feen.stream.model.domain.FleenStream;
import com.fleencorp.feen.stream.model.domain.StreamAttendee;
import com.fleencorp.feen.stream.model.projection.StreamAttendeeInfoSelect;
import com.fleencorp.feen.stream.model.projection.StreamAttendeeSelect;
import com.fleencorp.feen.stream.model.request.search.StreamAttendeeSearchRequest;
import com.fleencorp.feen.stream.model.response.StreamResponse;
import com.fleencorp.feen.stream.model.response.attendee.StreamAttendeeResponse;
import com.fleencorp.feen.stream.model.search.attendee.StreamAttendeeSearchResult;
import com.fleencorp.feen.user.model.domain.Member;
import com.fleencorp.feen.shared.security.RegisteredUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface StreamAttendeeOperationsService {

  int countByStreamAndRequestToJoinStatusAndAttending(FleenStream stream, StreamAttendeeRequestToJoinStatus requestToJoinStatus, Boolean isAttending);

  long countByIds(Set<Long> ids);

  void approveAllAttendeeRequestInvitation(StreamAttendeeRequestToJoinStatus status, List<Long> attendeeIds);

  void markAllAttendeesAsSpeaker(List<Long> attendeeIds);

  Optional<StreamAttendee> findOrganizerByStream(FleenStream stream, Member member);

  Optional<StreamAttendee> findDistinctByEmail(String emailAddress);

  List<StreamAttendee> findAllByAttendeeIds(Set<Long> attendeeIds);

  List<StreamAttendee> findAllByStreamAndRequestToJoinStatus(FleenStream stream, StreamAttendeeRequestToJoinStatus requestToJoinStatus);

  Optional<StreamAttendee> findAttendeeByStreamAndUser(FleenStream stream, Member member);

  Optional<StreamAttendee> findAttendeeByIdAndStream(Long attendeeId, FleenStream stream);

  List<StreamAttendee> findAttendeesGoingToStream(Long streamId);

  Set<StreamAttendee> findAttendeesByIdsAndStreamIdAndStatuses(List<Long> speakerAttendeeIds, Long streamId, List<StreamAttendeeRequestToJoinStatus> statuses);

  Page<StreamAttendee> findByStreamAndStreamType(FleenStream stream, StreamType streamType, Pageable pageable);

  Page<StreamAttendee> findByStreamAndRequestToJoinStatus(FleenStream stream, Set<StreamAttendeeRequestToJoinStatus> requestToJoinStatuses, Pageable pageable);

  Page<StreamAttendee> findAttendeesGoingToStream(FleenStream stream, Pageable pageable);

  Page<StreamAttendee> findAllByStreamAndRequestToJoinStatusAndAttending(FleenStream stream, StreamAttendeeRequestToJoinStatus requestToJoinStatus, Boolean isAttending, Pageable pageable);

  Page<StreamAttendeeInfoSelect> findPotentialAttendeeSpeakersByStreamAndFullNameOrUsername(Long streamId, Long organizerId, String userIdOrName, Pageable pageable);

  List<StreamAttendeeSelect> findByMemberAndStreamIds(Member member, List<Long> streamIds);

  void saveAll(Collection<StreamAttendee> attendees);

  void save(StreamAttendee attendee);

  StreamAttendeeSearchResult findStreamAttendees(Long streamId, StreamAttendeeSearchRequest searchRequest);

  void checkIfAttendeeIsMemberOfChatSpaceAndSendInvitationForJoinStreamRequest(boolean isAttendeeMemberOfChatSpace, String streamExternalId, String comment, RegisteredUser user);

  Collection<StreamAttendeeResponse> getAttendeesGoingToStream(StreamResponse streamResponse);

  StreamAttendeeSearchResult getStreamAttendees(Long streamId, StreamAttendeeSearchRequest searchRequest) throws StreamNotFoundException;

  Optional<StreamAttendee> findAttendeeByMemberId(FleenStream stream, Long userId);

  Optional<StreamAttendee> findAttendee(FleenStream stream, Long attendeeId);

  RequestToJoinSearchResult getAttendeeRequestsToJoinStream(Long streamId, StreamAttendeeSearchRequest searchRequest, RegisteredUser user);

  StreamAttendee getExistingOrCreateNewStreamAttendee(FleenStream stream, String comment, RegisteredUser user) throws FailedOperationException;

  void createNewEventAttendeeRequestAndSendInvitation(String calendarExternalId, String streamExternalId, String attendeeEmailAddress, String comment);
}
