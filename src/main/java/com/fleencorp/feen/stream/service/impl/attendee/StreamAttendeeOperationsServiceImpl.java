package com.fleencorp.feen.stream.service.impl.attendee;

import com.fleencorp.feen.chat.space.model.search.core.RequestToJoinSearchResult;
import com.fleencorp.feen.common.exception.FailedOperationException;
import com.fleencorp.feen.shared.member.contract.IsAMember;
import com.fleencorp.feen.shared.stream.contract.IsAStream;
import com.fleencorp.feen.shared.stream.contract.IsAttendee;
import com.fleencorp.feen.stream.constant.attendee.StreamAttendeeRequestToJoinStatus;
import com.fleencorp.feen.stream.constant.core.StreamType;
import com.fleencorp.feen.stream.exception.core.StreamNotFoundException;
import com.fleencorp.feen.stream.model.domain.FleenStream;
import com.fleencorp.feen.stream.model.domain.StreamAttendee;
import com.fleencorp.feen.stream.model.projection.StreamAttendeeSelect;
import com.fleencorp.feen.stream.model.request.search.StreamAttendeeSearchRequest;
import com.fleencorp.feen.stream.model.response.StreamResponse;
import com.fleencorp.feen.stream.model.response.attendee.StreamAttendeeResponse;
import com.fleencorp.feen.stream.model.search.attendee.StreamAttendeeSearchResult;
import com.fleencorp.feen.stream.repository.attendee.StreamAttendeeManagementRepository;
import com.fleencorp.feen.stream.repository.attendee.StreamAttendeeProjectionRepository;
import com.fleencorp.feen.stream.repository.attendee.StreamAttendeeRepository;
import com.fleencorp.feen.stream.repository.attendee.StreamAttendeeSearchRepository;
import com.fleencorp.feen.stream.service.attendee.StreamAttendeeOperationsService;
import com.fleencorp.feen.stream.service.attendee.StreamAttendeeService;
import com.fleencorp.feen.stream.service.update.StreamAttendeeUpdateService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class StreamAttendeeOperationsServiceImpl implements StreamAttendeeOperationsService {

  private final StreamAttendeeService streamAttendeeService;
  private final StreamAttendeeUpdateService streamAttendeeUpdateService;
  private final StreamAttendeeRepository streamAttendeeRepository;
  private final StreamAttendeeManagementRepository streamAttendeeManagementRepository;
  private final StreamAttendeeProjectionRepository streamAttendeeProjectionRepository;
  private final StreamAttendeeSearchRepository streamAttendeeSearchRepository;

  public StreamAttendeeOperationsServiceImpl(
      final StreamAttendeeService streamAttendeeService,
      final StreamAttendeeUpdateService streamAttendeeUpdateService,
      final StreamAttendeeRepository streamAttendeeRepository,
      final StreamAttendeeManagementRepository streamAttendeeManagementRepository,
      final StreamAttendeeProjectionRepository streamAttendeeProjectionRepository,
      final StreamAttendeeSearchRepository streamAttendeeSearchRepository) {
    this.streamAttendeeService = streamAttendeeService;
    this.streamAttendeeUpdateService = streamAttendeeUpdateService;
    this.streamAttendeeRepository = streamAttendeeRepository;
    this.streamAttendeeManagementRepository = streamAttendeeManagementRepository;
    this.streamAttendeeProjectionRepository = streamAttendeeProjectionRepository;
    this.streamAttendeeSearchRepository = streamAttendeeSearchRepository;
  }

  @Override
  public int countByStreamAndRequestToJoinStatusAndAttending(final FleenStream stream, final StreamAttendeeRequestToJoinStatus requestToJoinStatus, final Boolean isAttending) {
    return streamAttendeeManagementRepository.countByStreamAndRequestToJoinStatusAndAttending(
      stream, requestToJoinStatus, isAttending);
  }

  @Override
  public long countByIds(final Set<Long> ids) {
    return streamAttendeeManagementRepository.countByIds(ids);
  }

  @Override
  @Transactional
  public void approveAllAttendeeRequestInvitation(final StreamAttendeeRequestToJoinStatus status, final List<Long> attendeeIds) {
    streamAttendeeManagementRepository.approveAllAttendeeRequestInvitation(status, attendeeIds);
  }

  @Override
  @Transactional
  public void markAllAttendeesAsSpeaker(final List<Long> attendeeIds) {
    streamAttendeeManagementRepository.markAllAttendeesAsSpeaker(attendeeIds);
  }

  @Override
  public Optional<StreamAttendee> findOrganizerByStream(final IsAStream stream, final IsAMember member) {
    return streamAttendeeSearchRepository.findOrganizerByStream(stream.getStreamId(), member.getMemberId());
  }

  @Override
  public Optional<StreamAttendee> findDistinctByEmail(final String emailAddress) {
    return streamAttendeeSearchRepository.findDistinctByEmail(emailAddress);
  }

  @Override
  public List<StreamAttendee> findAllByAttendeeIds(final Set<Long> attendeeIds) {
    return streamAttendeeSearchRepository.findAllByAttendeeIds(attendeeIds);
  }

  @Override
  public List<IsAttendee> findAllByStreamAndRequestToJoinStatus(final IsAStream stream, final StreamAttendeeRequestToJoinStatus requestToJoinStatus) {
    return streamAttendeeSearchRepository.findAllByStreamAndRequestToJoinStatus(stream.getStreamId(), requestToJoinStatus);
  }

  @Override
  public Optional<StreamAttendee> findAttendeeByStreamAndUser(final Long streamId, final Long memberId) {
    return streamAttendeeSearchRepository.findAttendeeByStreamAndUser(streamId, memberId);
  }

  @Override
  public Optional<StreamAttendee> findAttendeeByIdAndStream(final Long attendeeId, final Long streamId) {
    return streamAttendeeSearchRepository.findAttendeeByIdAndStream(attendeeId, streamId);
  }

  @Override
  public List<IsAttendee> findAttendeesGoingToStream(final Long streamId) {
    return streamAttendeeSearchRepository.findAttendeesGoingToStream(streamId);
  }

  @Override
  public Set<StreamAttendee> findAttendeesByIdsAndStreamIdAndStatuses(final List<Long> speakerAttendeeIds, final Long streamId, final List<StreamAttendeeRequestToJoinStatus> statuses) {
    return streamAttendeeSearchRepository.findAttendeesByIdsAndStreamIdAndStatuses(speakerAttendeeIds, streamId, statuses);
  }

  @Override
  public Page<IsAttendee> findByStreamAndStreamType(final IsAStream stream, final StreamType streamType, final Pageable pageable) {
    return streamAttendeeRepository.findByStreamAndStreamType(stream.getStreamId(), streamType, pageable);
  }

  @Override
  public Page<IsAttendee> findByStreamAndRequestToJoinStatus(final IsAStream stream, final Set<StreamAttendeeRequestToJoinStatus> requestToJoinStatuses, final Pageable pageable) {
    return streamAttendeeRepository.findByStreamAndRequestToJoinStatus(stream.getStreamId(), requestToJoinStatuses, pageable);
  }

  @Override
  public Page<IsAttendee> findAttendeesGoingToStream(final IsAStream stream, final Pageable pageable) {
    return streamAttendeeRepository.findAttendeesGoingToStream(stream.getStreamId(), pageable);
  }

  @Override
  public Page<IsAttendee> findAllByStreamAndRequestToJoinStatusAndAttending(final IsAStream stream, final StreamAttendeeRequestToJoinStatus requestToJoinStatus, final Boolean isAttending, final Pageable pageable) {
    return streamAttendeeRepository.findAllByStreamAndRequestToJoinStatusAndAttending(
      stream.getStreamId(), requestToJoinStatus, isAttending, pageable);
  }

  @Override
  public Page<IsAttendee> findPotentialAttendeeSpeakersByStreamAndFullNameOrUsername(
    final Long streamId, final Long organizerId, final String userIdOrName, final Pageable pageable) {
    return streamAttendeeProjectionRepository.findPotentialAttendeeSpeakersByStreamAndFullNameOrUsername(
      streamId, organizerId, userIdOrName, pageable);
  }

  @Override
  public List<StreamAttendeeSelect> findByMemberAndStreamIds(final IsAMember member, final List<Long> streamIds) {
    return streamAttendeeProjectionRepository.findByMemberAndStreamIds(member.getMemberId(), streamIds);
  }

  @Override
  @Transactional
  public void saveAll(final Collection<StreamAttendee> attendees) {
    streamAttendeeRepository.saveAll(attendees);
  }

  @Override
  @Transactional
  public void save(final StreamAttendee attendee) {
    streamAttendeeRepository.save(attendee);
  }

  @Override
  public StreamAttendeeSearchResult findStreamAttendees(final Long streamId, final StreamAttendeeSearchRequest searchRequest) {
    return streamAttendeeService.findStreamAttendees(streamId, searchRequest);
  }

  @Override
  public void checkIfAttendeeIsMemberOfChatSpaceAndSendInvitationForJoinStreamRequest(
    final boolean isAttendeeMemberOfChatSpace, final String streamExternalId, final String comment, final IsAMember user) {
    streamAttendeeService.checkIfAttendeeIsMemberOfChatSpaceAndSendInvitationForJoinStreamRequest(
      isAttendeeMemberOfChatSpace, streamExternalId, comment, user);
  }

  @Override
  public Collection<StreamAttendeeResponse> getAttendeesGoingToStream(final StreamResponse streamResponse) {
    return streamAttendeeService.getAttendeesGoingToStream(streamResponse);
  }

  @Override
  public StreamAttendeeSearchResult getStreamAttendees(final Long streamId, final StreamAttendeeSearchRequest searchRequest) throws StreamNotFoundException {
    return streamAttendeeService.getStreamAttendees(streamId, searchRequest);
  }

  @Override
  public Optional<StreamAttendee> findAttendeeByMemberId(final Long streamId, final Long userId) {
    return streamAttendeeService.findAttendeeByMemberId(streamId, userId);
  }

  @Override
  public Optional<StreamAttendee> findAttendee(final Long streamId, final Long attendeeId) {
    return streamAttendeeService.findAttendee(streamId, attendeeId);
  }

  @Override
  public RequestToJoinSearchResult getAttendeeRequestsToJoinStream(final Long streamId, final StreamAttendeeSearchRequest searchRequest, final IsAMember user) {
    return streamAttendeeService.getAttendeeRequestsToJoinStream(streamId, searchRequest, user);
  }

  @Override
  public StreamAttendee getExistingOrCreateNewStreamAttendee(final IsAStream stream, final String comment, final IsAMember user) throws FailedOperationException {
    return streamAttendeeService.getExistingOrCreateNewStreamAttendee(stream, comment, user);
  }

  @Override
  public void createNewEventAttendeeRequestAndSendInvitation(final String calendarExternalId, final String streamExternalId, final String attendeeEmailAddress, final String comment) {
    streamAttendeeUpdateService.createNewEventAttendeeRequestAndSendInvitation(calendarExternalId, streamExternalId, attendeeEmailAddress, comment);
  }
}
