package com.fleencorp.feen.stream.service.impl.attendee;

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
import com.fleencorp.feen.stream.repository.attendee.StreamAttendeeManagementRepository;
import com.fleencorp.feen.stream.repository.attendee.StreamAttendeeProjectionRepository;
import com.fleencorp.feen.stream.repository.attendee.StreamAttendeeRepository;
import com.fleencorp.feen.stream.repository.attendee.StreamAttendeeSearchRepository;
import com.fleencorp.feen.stream.service.attendee.StreamAttendeeOperationsService;
import com.fleencorp.feen.stream.service.attendee.StreamAttendeeService;
import com.fleencorp.feen.stream.service.update.StreamAttendeeUpdateService;
import com.fleencorp.feen.user.model.domain.Member;
import com.fleencorp.feen.user.model.security.RegisteredUser;
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
  public Optional<StreamAttendee> findOrganizerByStream(final FleenStream stream, final Member member) {
    return streamAttendeeSearchRepository.findOrganizerByStream(stream, member);
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
  public List<StreamAttendee> findAllByStreamAndRequestToJoinStatus(final FleenStream stream, final StreamAttendeeRequestToJoinStatus requestToJoinStatus) {
    return streamAttendeeSearchRepository.findAllByStreamAndRequestToJoinStatus(stream, requestToJoinStatus);
  }

  @Override
  public Optional<StreamAttendee> findAttendeeByStreamAndUser(final FleenStream stream, final Member member) {
    return streamAttendeeSearchRepository.findAttendeeByStreamAndUser(stream, member);
  }

  @Override
  public Optional<StreamAttendee> findAttendeeByIdAndStream(final Long attendeeId, final FleenStream stream) {
    return streamAttendeeSearchRepository.findAttendeeByIdAndStream(attendeeId, stream);
  }

  @Override
  public List<StreamAttendee> findAttendeesGoingToStream(final Long streamId) {
    return streamAttendeeSearchRepository.findAttendeesGoingToStream(streamId);
  }

  @Override
  public Set<StreamAttendee> findAttendeesByIdsAndStreamIdAndStatuses(final List<Long> speakerAttendeeIds, final Long streamId, final List<StreamAttendeeRequestToJoinStatus> statuses) {
    return streamAttendeeSearchRepository.findAttendeesByIdsAndStreamIdAndStatuses(speakerAttendeeIds, streamId, statuses);
  }

  @Override
  public Page<StreamAttendee> findByStreamAndStreamType(final FleenStream stream, final StreamType streamType, final Pageable pageable) {
    return streamAttendeeRepository.findByStreamAndStreamType(stream, streamType, pageable);
  }

  @Override
  public Page<StreamAttendee> findByStreamAndRequestToJoinStatus(final FleenStream stream, final Set<StreamAttendeeRequestToJoinStatus> requestToJoinStatuses, final Pageable pageable) {
    return streamAttendeeRepository.findByStreamAndRequestToJoinStatus(stream, requestToJoinStatuses, pageable);
  }

  @Override
  public Page<StreamAttendee> findAttendeesGoingToStream(final FleenStream stream, final Pageable pageable) {
    return streamAttendeeRepository.findAttendeesGoingToStream(stream, pageable);
  }

  @Override
  public Page<StreamAttendee> findAllByStreamAndRequestToJoinStatusAndAttending(final FleenStream stream, final StreamAttendeeRequestToJoinStatus requestToJoinStatus, final Boolean isAttending, final Pageable pageable) {
    return streamAttendeeRepository.findAllByStreamAndRequestToJoinStatusAndAttending(
      stream, requestToJoinStatus, isAttending, pageable);
  }

  @Override
  public Page<StreamAttendeeInfoSelect> findPotentialAttendeeSpeakersByStreamAndFullNameOrUsername(
    final Long streamId, final Long organizerId, final String userIdOrName, final Pageable pageable) {
    return streamAttendeeProjectionRepository.findPotentialAttendeeSpeakersByStreamAndFullNameOrUsername(
      streamId, organizerId, userIdOrName, pageable);
  }

  @Override
  public List<StreamAttendeeSelect> findByMemberAndStreamIds(final Member member, final List<Long> streamIds) {
    return streamAttendeeProjectionRepository.findByMemberAndStreamIds(member, streamIds);
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
    final boolean isAttendeeMemberOfChatSpace, final String streamExternalId, final String comment, final RegisteredUser user) {
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
  public Optional<StreamAttendee> findAttendeeByMemberId(final FleenStream stream, final Long userId) {
    return streamAttendeeService.findAttendeeByMemberId(stream, userId);
  }

  @Override
  public Optional<StreamAttendee> findAttendee(final FleenStream stream, final Long attendeeId) {
    return streamAttendeeService.findAttendee(stream, attendeeId);
  }

  @Override
  public RequestToJoinSearchResult getAttendeeRequestsToJoinStream(final Long streamId, final StreamAttendeeSearchRequest searchRequest, final RegisteredUser user) {
    return streamAttendeeService.getAttendeeRequestsToJoinStream(streamId, searchRequest, user);
  }

  @Override
  public StreamAttendee getExistingOrCreateNewStreamAttendee(final FleenStream stream, final String comment, final RegisteredUser user) throws FailedOperationException {
    return streamAttendeeService.getExistingOrCreateNewStreamAttendee(stream, comment, user);
  }

  @Override
  public void createNewEventAttendeeRequestAndSendInvitation(final String calendarExternalId, final String streamExternalId, final String attendeeEmailAddress, final String comment) {
    streamAttendeeUpdateService.createNewEventAttendeeRequestAndSendInvitation(calendarExternalId, streamExternalId, attendeeEmailAddress, comment);
  }
}
