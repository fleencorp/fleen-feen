package com.fleencorp.feen.service.impl.stream;

import com.fleencorp.feen.constant.stream.StreamStatus;
import com.fleencorp.feen.constant.stream.StreamType;
import com.fleencorp.feen.constant.stream.StreamVisibility;
import com.fleencorp.feen.constant.stream.attendee.StreamAttendeeRequestToJoinStatus;
import com.fleencorp.feen.exception.calendar.CalendarNotFoundException;
import com.fleencorp.feen.exception.google.oauth2.Oauth2InvalidAuthorizationException;
import com.fleencorp.feen.exception.stream.StreamNotFoundException;
import com.fleencorp.feen.model.domain.stream.FleenStream;
import com.fleencorp.feen.model.domain.user.Member;
import com.fleencorp.feen.model.holder.StreamOtherDetailsHolder;
import com.fleencorp.feen.model.response.stream.StreamResponse;
import com.fleencorp.feen.model.response.stream.common.DataForRescheduleStreamResponse;
import com.fleencorp.feen.model.security.FleenUser;
import com.fleencorp.feen.repository.stream.stream.StreamManagementRepository;
import com.fleencorp.feen.repository.stream.stream.StreamQueryRepository;
import com.fleencorp.feen.repository.stream.stream.StreamRepository;
import com.fleencorp.feen.repository.stream.user.UserStreamCountRepository;
import com.fleencorp.feen.repository.stream.user.UserStreamQueryRepository;
import com.fleencorp.feen.service.stream.StreamOperationsService;
import com.fleencorp.feen.service.stream.common.CommonStreamOtherService;
import com.fleencorp.feen.service.stream.common.StreamService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

@Service
public class StreamOperationsServiceImpl implements StreamOperationsService {

  private final CommonStreamOtherService commonStreamOtherService;
  private final StreamService streamService;
  private final StreamRepository streamRepository;
  private final StreamQueryRepository streamQueryRepository;
  private final StreamManagementRepository streamManagementRepository;
  private final UserStreamQueryRepository userStreamQueryRepository;
  private final UserStreamCountRepository userStreamCountRepository;

  public StreamOperationsServiceImpl(
      final CommonStreamOtherService commonStreamOtherService,
      final StreamService streamService,
      final StreamRepository streamRepository,
      final StreamQueryRepository streamQueryRepository,
      final StreamManagementRepository streamManagementRepository,
      final UserStreamQueryRepository userStreamQueryRepository,
      final UserStreamCountRepository userStreamCountRepository) {
    this.commonStreamOtherService = commonStreamOtherService;
    this.streamService = streamService;
    this.streamRepository = streamRepository;
    this.streamQueryRepository = streamQueryRepository;
    this.streamManagementRepository = streamManagementRepository;
    this.userStreamQueryRepository = userStreamQueryRepository;
    this.userStreamCountRepository = userStreamCountRepository;
  }

  @Override
  public Page<FleenStream> findByDateBetween(final LocalDateTime startDate, final LocalDateTime endDate, final StreamStatus status, final Pageable pageable) {
    return streamQueryRepository.findByDateBetween(startDate, endDate, status, pageable);
  }

  @Override
  public Page<FleenStream> findByTitle(final String title, final StreamStatus status, final Pageable pageable) {
    return streamQueryRepository.findByTitle(title, status, pageable);
  }

  @Override
  public Page<FleenStream> findMany(final StreamStatus status, final Pageable pageable) {
    return streamQueryRepository.findMany(status, pageable);
  }

  @Override
  public Page<FleenStream> findUpcomingStreams(final LocalDateTime dateTime, final StreamType streamType, final Pageable pageable) {
    return streamQueryRepository.findUpcomingStreams(dateTime, streamType, pageable);
  }

  @Override
  public Page<FleenStream> findUpcomingStreamsByTitle(final String title, final LocalDateTime dateTime, final StreamType streamType, final Pageable pageable) {
    return streamQueryRepository.findUpcomingStreamsByTitle(title, dateTime, streamType, pageable);
  }

  @Override
  public Page<FleenStream> findPastStreams(final LocalDateTime dateTime, final StreamType streamType, final Pageable pageable) {
    return streamQueryRepository.findPastStreams(dateTime, streamType, pageable);
  }

  @Override
  public Page<FleenStream> findPastStreamsByTitle(final String title, final LocalDateTime dateTime, final StreamType streamType, final Pageable pageable) {
    return streamQueryRepository.findPastStreamsByTitle(title, dateTime, streamType, pageable);
  }

  @Override
  public Page<FleenStream> findLiveStreams(final LocalDateTime dateTime, final StreamType streamType, final Pageable pageable) {
    return streamQueryRepository.findLiveStreams(dateTime, streamType, pageable);
  }

  @Override
  public Page<FleenStream> findLiveStreamsByTitle(final String title, final LocalDateTime dateTime, final StreamType streamType, final Pageable pageable) {
    return streamQueryRepository.findLiveStreamsByTitle(title, dateTime, streamType, pageable);
  }

  @Override
  public Page<FleenStream> findByChatSpaceId(final Long chatSpaceId, final Pageable pageable) {
    return streamQueryRepository.findByChatSpaceId(chatSpaceId, pageable);
  }

  @Override
  @Transactional
  public void incrementTotalAttendees(final Long streamId) {
    streamManagementRepository.incrementTotalAttendees(streamId);
  }

  @Override
  @Transactional
  public void decrementTotalAttendees(final Long streamId) {
    streamManagementRepository.decrementTotalAttendees(streamId);
  }

  @Override
  @Transactional
  public int incrementAndGetLikeCount(final Long streamId) {
    streamManagementRepository.incrementAndGetLikeCount(streamId);
    return streamManagementRepository.getLikeCount(streamId);
  }

  @Override
  @Transactional
  public int decrementAndGetLikeCount(final Long streamId) {
    streamManagementRepository.decrementAndGetLikeCount(streamId);
    return streamManagementRepository.getLikeCount(streamId);
  }

  @Override
  public Page<FleenStream> findManyByMe(final Member member, final Pageable pageable) {
    return userStreamQueryRepository.findManyByMe(member, pageable);
  }

  @Override
  public Page<FleenStream> findByTitleAndUser(final String title, final Member member, final Pageable pageable) {
    return userStreamQueryRepository.findByTitleAndUser(title, member, pageable);
  }

  @Override
  public Page<FleenStream> findByTitleAndUser(final String title, final StreamVisibility streamVisibility, final Member member, final Pageable pageable) {
    return userStreamQueryRepository.findByTitleAndUser(title, streamVisibility, member, pageable);
  }

  @Override
  public Page<FleenStream> findByDateBetweenAndUser(final LocalDateTime startDate, final LocalDateTime endDate, final Member member, final Pageable pageable) {
    return userStreamQueryRepository.findByDateBetweenAndUser(startDate, endDate, member, pageable);
  }

  @Override
  public Page<FleenStream> findByDateBetweenAndUser(final LocalDateTime startDate, final LocalDateTime endDate, final StreamVisibility streamVisibility, final Member member, final Pageable pageable) {
    return userStreamQueryRepository.findByDateBetweenAndUser(startDate, endDate, streamVisibility, member, pageable);
  }

  @Override
  public Page<FleenStream> findAttendedByUser(final Member member, final Pageable pageable) {
    return userStreamQueryRepository.findAttendedByUser(member, pageable);
  }

  @Override
  public Page<FleenStream> findAttendedByDateBetweenAndUser(
    final LocalDateTime startDate, final LocalDateTime endDate, final Member member, final Pageable pageable) {
    return userStreamQueryRepository.findAttendedByDateBetweenAndUser(startDate, endDate, member, pageable);
  }

  @Override
  public Page<FleenStream> findAttendedByTitleAndUser(final String title, final Member member, final Pageable pageable) {
    return userStreamQueryRepository.findAttendedByTitleAndUser(title, member, pageable);
  }

  @Override
  public Page<FleenStream> findStreamsAttendedTogether(final Member you, final Member friend, final Pageable pageable) {
    return userStreamQueryRepository.findStreamsAttendedTogether(you, friend, pageable);
  }

  @Override
  public Long countTotalStreamsAttended(final Member member) {
    return userStreamCountRepository.countTotalStreamsAttended(member);
  }

  @Override
  public Long countTotalStreamsAttended(final StreamType streamType, final Member member) {
    return userStreamCountRepository.countTotalStreamsAttended(streamType, member);
  }

  @Override
  public Long countTotalStreamsByUser(final Member member) {
    return userStreamCountRepository.countTotalStreamsByUser(member);
  }

  @Override
  public Long countTotalStreamsByUser(final StreamType streamType, final Member member) {
    return userStreamCountRepository.countTotalStreamsByUser(streamType, member);
  }

  @Override
  public FleenStream findStream(final Long streamId) throws StreamNotFoundException {
    return streamService.findStream(streamId);
  }

  @Override
  public DataForRescheduleStreamResponse getDataForRescheduleStream() {
    return streamService.getDataForRescheduleStream();
  }

  @Override
  public void increaseTotalAttendeesOrGuests(final FleenStream stream) {
    streamService.increaseTotalAttendeesOrGuests(stream);
  }

  @Override
  public void validateStreamAndUserForProtectedStream(final FleenStream stream, final FleenUser user) {
    streamService.validateStreamAndUserForProtectedStream(stream, user);
  }

  @Override
  public void registerAndApproveOrganizerOfStreamAsAnAttendee(final FleenStream stream, final FleenUser user) {
    streamService.registerAndApproveOrganizerOfStreamAsAnAttendee(stream, user);
  }

  @Override
  public StreamOtherDetailsHolder retrieveStreamOtherDetailsHolder(final FleenStream stream, final FleenUser user) throws CalendarNotFoundException, Oauth2InvalidAuthorizationException {
    return streamService.retrieveStreamOtherDetailsHolder(stream, user);
  }

  @Override
  public boolean existsByAttendees(final Member viewer, final Member target) {
    return streamService.existsByAttendees(viewer, target);
  }

  @Override
  public Long incrementLikeCount(final Long streamId) {
    return streamService.incrementLikeCount(streamId);
  }

  @Override
  public Long decrementLikeCount(final Long streamId) {
    return streamService.decrementLikeCount(streamId);
  }

  @Override
  public FleenStream save(final FleenStream stream) {
    return streamManagementRepository.save(stream);
  }

  @Override
  public Optional<FleenStream> findById(final Long streamId) {
    return streamManagementRepository.findById(streamId);
  }

  @Override
  public boolean existsByAttendees(final Long memberId, final Long targetMemberId) {
    return false;
  }

  @Override
  public void processOtherStreamDetails(final Collection<StreamResponse> streamResponses, final Member member) {
    commonStreamOtherService.processOtherStreamDetails(streamResponses, member);
  }

  @Override
  public void setStreamAttendeesAndTotalAttendeesAttending(final StreamResponse streamResponse) {
    commonStreamOtherService.setStreamAttendeesAndTotalAttendeesAttending(streamResponse);
  }

  @Override
  public void setFirst10AttendeesAttendingInAnyOrderOnStreams(final StreamResponse streamResponse) {
    commonStreamOtherService.setFirst10AttendeesAttendingInAnyOrderOnStreams(streamResponse);
  }

  @Override
  public Page<FleenStream> findStreamsCreatedByMember(final Long memberId, final Collection<StreamStatus> includedStatuses, final Pageable pageable) {
    return streamRepository.findStreamsCreatedByMember(memberId, includedStatuses, pageable);
  }

  @Override
  public Page<FleenStream> findCommonPastAttendedStreams(final Long memberAId, final Long memberBId, final Collection<StreamAttendeeRequestToJoinStatus> approvedStatuses, final Collection<StreamStatus> includedStatuses, final Pageable pageable) {
    return streamRepository.findCommonPastAttendedStreams(memberAId, memberBId, approvedStatuses, includedStatuses, pageable);
  }
}


