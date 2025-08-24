package com.fleencorp.feen.stream.service.impl.common;

import com.fleencorp.feen.calendar.exception.core.CalendarNotFoundException;
import com.fleencorp.feen.oauth2.exception.core.Oauth2InvalidAuthorizationException;
import com.fleencorp.feen.stream.constant.attendee.StreamAttendeeRequestToJoinStatus;
import com.fleencorp.feen.stream.constant.core.StreamStatus;
import com.fleencorp.feen.stream.constant.core.StreamType;
import com.fleencorp.feen.stream.exception.core.StreamNotFoundException;
import com.fleencorp.feen.stream.model.domain.FleenStream;
import com.fleencorp.feen.stream.model.holder.StreamOtherDetailsHolder;
import com.fleencorp.feen.stream.model.response.StreamResponse;
import com.fleencorp.feen.stream.model.response.common.DataForRescheduleStreamResponse;
import com.fleencorp.feen.stream.repository.core.StreamManagementRepository;
import com.fleencorp.feen.stream.repository.core.StreamRepository;
import com.fleencorp.feen.stream.repository.core.StreamSearchRepository;
import com.fleencorp.feen.stream.repository.user.UserStreamCountRepository;
import com.fleencorp.feen.stream.service.common.StreamOperationsService;
import com.fleencorp.feen.stream.service.core.CommonStreamOtherService;
import com.fleencorp.feen.stream.service.core.StreamService;
import com.fleencorp.feen.user.model.domain.Member;
import com.fleencorp.feen.user.model.security.RegisteredUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;

@Service
public class StreamOperationsServiceImpl implements StreamOperationsService {

  private final CommonStreamOtherService commonStreamOtherService;
  private final StreamService streamService;
  private final StreamRepository streamRepository;
  private final StreamSearchRepository streamSearchRepository;
  private final StreamManagementRepository streamManagementRepository;
  private final UserStreamCountRepository userStreamCountRepository;

  public StreamOperationsServiceImpl(
      final CommonStreamOtherService commonStreamOtherService,
      final StreamService streamService,
      final StreamRepository streamRepository,
      final StreamSearchRepository streamSearchRepository,
      final StreamManagementRepository streamManagementRepository,
      final UserStreamCountRepository userStreamCountRepository) {
    this.commonStreamOtherService = commonStreamOtherService;
    this.streamService = streamService;
    this.streamRepository = streamRepository;
    this.streamSearchRepository = streamSearchRepository;
    this.streamManagementRepository = streamManagementRepository;
    this.userStreamCountRepository = userStreamCountRepository;
  }

  @Override
  public Page<FleenStream> findByChatSpaceId(final Long chatSpaceId, final Pageable pageable) {
    return streamSearchRepository.findByChatSpaceId(chatSpaceId, pageable);
  }

  @Override
  @Transactional
  public void updateTotalAttendeeCount(final Long streamId, final boolean increment) {
    if (increment) {
      streamManagementRepository.incrementTotalAttendees(streamId);
    } else {
      streamManagementRepository.decrementTotalAttendees(streamId);
    }
  }

  private int incrementAndGetLikeCount(final Long streamId) {
    streamManagementRepository.incrementAndGetLikeCount(streamId);
    return streamManagementRepository.getLikeCount(streamId);
  }

  private int decrementAndGetLikeCount(final Long streamId) {
    streamManagementRepository.decrementAndGetLikeCount(streamId);
    return streamManagementRepository.getLikeCount(streamId);
  }

  private int incrementAndGetBookmarkCount(final Long streamId) {
    streamManagementRepository.incrementAndGetBookmarkCount(streamId);
    return streamManagementRepository.getBookmarkCount(streamId);
  }

  private int decrementAndGetBookmarkCount(final Long streamId) {
    streamManagementRepository.decrementAndGetBookmarkCount(streamId);
    return streamManagementRepository.getBookmarkCount(streamId);
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
  public void validateStreamAndUserForProtectedStream(final FleenStream stream, final RegisteredUser user) {
    streamService.validateStreamAndUserForProtectedStream(stream, user);
  }

  @Override
  public void registerAndApproveOrganizerOfStreamAsAnAttendee(final FleenStream stream, final RegisteredUser user) {
    streamService.registerAndApproveOrganizerOfStreamAsAnAttendee(stream, user);
  }

  @Override
  public StreamOtherDetailsHolder retrieveStreamOtherDetailsHolder(final FleenStream stream, final RegisteredUser user) throws CalendarNotFoundException, Oauth2InvalidAuthorizationException {
    return streamService.retrieveStreamOtherDetailsHolder(stream, user);
  }

  @Override
  public boolean existsByAttendees(final Member viewer, final Member target) {
    return streamService.existsByAttendees(viewer, target);
  }

  @Override
  @Transactional
  public Integer updateLikeCount(final Long streamId, final boolean isLiked) {
    return isLiked ? incrementAndGetLikeCount(streamId) : decrementAndGetLikeCount(streamId);
  }

  @Override
  @Transactional
  public Integer updateBookmarkCount(final Long streamId, final boolean bookmarked) {
    if (bookmarked) {
      return incrementAndGetBookmarkCount(streamId);
    } else {
      return decrementAndGetBookmarkCount(streamId);
    }
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