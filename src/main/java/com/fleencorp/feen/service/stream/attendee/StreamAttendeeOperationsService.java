package com.fleencorp.feen.service.stream.attendee;

import com.fleencorp.feen.constant.stream.StreamType;
import com.fleencorp.feen.constant.stream.attendee.StreamAttendeeRequestToJoinStatus;
import com.fleencorp.feen.exception.base.FailedOperationException;
import com.fleencorp.feen.exception.stream.StreamNotFoundException;
import com.fleencorp.feen.model.domain.stream.FleenStream;
import com.fleencorp.feen.model.domain.stream.StreamAttendee;
import com.fleencorp.feen.user.model.domain.Member;
import com.fleencorp.feen.model.projection.stream.attendee.StreamAttendeeInfoSelect;
import com.fleencorp.feen.model.projection.stream.attendee.StreamAttendeeSelect;
import com.fleencorp.feen.model.request.search.stream.StreamAttendeeSearchRequest;
import com.fleencorp.feen.model.response.stream.StreamResponse;
import com.fleencorp.feen.model.response.stream.attendee.StreamAttendeeResponse;
import com.fleencorp.feen.model.search.join.RequestToJoinSearchResult;
import com.fleencorp.feen.model.search.stream.attendee.StreamAttendeeSearchResult;
import com.fleencorp.feen.model.security.FleenUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface StreamAttendeeOperationsService {

  long countByStreamAndRequestToJoinStatusAndAttending(FleenStream stream, StreamAttendeeRequestToJoinStatus requestToJoinStatus, Boolean isAttending);

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

  void checkIfAttendeeIsMemberOfChatSpaceAndSendInvitationForJoinStreamRequest(boolean isAttendeeMemberOfChatSpace, String streamExternalId, String comment, FleenUser user);

  Collection<StreamAttendeeResponse> getAttendeesGoingToStream(StreamResponse streamResponse);

  StreamAttendeeSearchResult getStreamAttendees(Long streamId, StreamAttendeeSearchRequest searchRequest) throws StreamNotFoundException;

  Optional<StreamAttendee> findAttendeeByMemberId(FleenStream stream, Long userId);

  Optional<StreamAttendee> findAttendee(FleenStream stream, Long attendeeId);

  RequestToJoinSearchResult getAttendeeRequestsToJoinStream(Long streamId, StreamAttendeeSearchRequest searchRequest, FleenUser user);

  StreamAttendee getExistingOrCreateNewStreamAttendee(FleenStream stream, String comment, FleenUser user) throws FailedOperationException;

  void createNewEventAttendeeRequestAndSendInvitation(String calendarExternalId, String streamExternalId, String attendeeEmailAddress, String comment);
}
