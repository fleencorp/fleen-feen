package com.fleencorp.feen.stream.service.impl.common;

import com.fleencorp.feen.shared.member.contract.IsAMember;
import com.fleencorp.feen.stream.constant.core.StreamStatus;
import com.fleencorp.feen.stream.constant.core.StreamType;
import com.fleencorp.feen.stream.constant.core.StreamVisibility;
import com.fleencorp.feen.stream.exception.core.StreamNotFoundException;
import com.fleencorp.feen.stream.model.domain.FleenStream;
import com.fleencorp.feen.stream.repository.core.StreamManagementRepository;
import com.fleencorp.feen.stream.repository.core.StreamSearchRepository;
import com.fleencorp.feen.stream.repository.user.UserStreamSearchRepository;
import com.fleencorp.feen.stream.service.common.StreamQueryService;
import com.fleencorp.feen.stream.service.core.StreamService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service("streamQueryService")
public class StreamQueryServiceImpl implements StreamQueryService {

  private final StreamService streamService;
  private final StreamSearchRepository streamSearchRepository;
  private final StreamManagementRepository streamManagementRepository;
  private final UserStreamSearchRepository userStreamSearchRepository;

  public StreamQueryServiceImpl(
      final StreamService streamService,
      final StreamSearchRepository streamSearchRepository,
      final StreamManagementRepository streamManagementRepository,
      final UserStreamSearchRepository userStreamSearchRepository) {
    this.streamService = streamService;
    this.streamSearchRepository = streamSearchRepository;
    this.streamManagementRepository = streamManagementRepository;
    this.userStreamSearchRepository = userStreamSearchRepository;
  }

  @Override
  public Page<FleenStream> findByDateBetween(final LocalDateTime startDate, final LocalDateTime endDate, final StreamStatus status, final Pageable pageable) {
    return streamSearchRepository.findByDateBetween(startDate, endDate, status, pageable);
  }

  @Override
  public Page<FleenStream> findByTitle(final String title, final StreamStatus status, final Pageable pageable) {
    return streamSearchRepository.findByTitle(title, status, pageable);
  }

  @Override
  public Page<FleenStream> findMany(final StreamStatus status, final Pageable pageable) {
    return streamSearchRepository.findMany(status, pageable);
  }

  @Override
  public Page<FleenStream> findUpcomingStreams(final LocalDateTime dateTime, final StreamType streamType, final Pageable pageable) {
    return streamSearchRepository.findUpcomingStreams(dateTime, streamType, pageable);
  }

  @Override
  public Page<FleenStream> findUpcomingStreamsByTitle(final String title, final LocalDateTime dateTime, final StreamType streamType, final Pageable pageable) {
    return streamSearchRepository.findUpcomingStreamsByTitle(title, dateTime, streamType, pageable);
  }

  @Override
  public Page<FleenStream> findPastStreams(final LocalDateTime dateTime, final StreamType streamType, final Pageable pageable) {
    return streamSearchRepository.findPastStreams(dateTime, streamType, pageable);
  }

  @Override
  public Page<FleenStream> findPastStreamsByTitle(final String title, final LocalDateTime dateTime, final StreamType streamType, final Pageable pageable) {
    return streamSearchRepository.findPastStreamsByTitle(title, dateTime, streamType, pageable);
  }

  @Override
  public Page<FleenStream> findLiveStreams(final LocalDateTime dateTime, final StreamType streamType, final Pageable pageable) {
    return streamSearchRepository.findLiveStreams(dateTime, streamType, pageable);
  }

  @Override
  public Page<FleenStream> findLiveStreamsByTitle(final String title, final LocalDateTime dateTime, final StreamType streamType, final Pageable pageable) {
    return streamSearchRepository.findLiveStreamsByTitle(title, dateTime, streamType, pageable);
  }

  @Override
  public Page<FleenStream> findManyByMe(final IsAMember member, final Pageable pageable) {
    return userStreamSearchRepository.findManyByMe(member.getMemberId(), pageable);
  }

  @Override
  public Page<FleenStream> findByTitleAndUser(final String title, final IsAMember member, final Pageable pageable) {
    return userStreamSearchRepository.findByTitleAndUser(title, member.getMemberId(), pageable);
  }

  @Override
  public Page<FleenStream> findByTitleAndUser(final String title, final StreamVisibility streamVisibility, final IsAMember member, final Pageable pageable) {
    return userStreamSearchRepository.findByTitleAndUser(title, streamVisibility, member.getMemberId(), pageable);
  }

  @Override
  public Page<FleenStream> findByDateBetweenAndUser(final LocalDateTime startDate, final LocalDateTime endDate, final IsAMember member, final Pageable pageable) {
    return userStreamSearchRepository.findByDateBetweenAndUser(startDate, endDate, member.getMemberId(), pageable);
  }

  @Override
  public Page<FleenStream> findByDateBetweenAndUser(final LocalDateTime startDate, final LocalDateTime endDate, final StreamVisibility streamVisibility, final IsAMember member, final Pageable pageable) {
    return userStreamSearchRepository.findByDateBetweenAndUser(startDate, endDate, streamVisibility, member.getMemberId(), pageable);
  }

  @Override
  public Page<FleenStream> findAttendedByUser(final IsAMember member, final Pageable pageable) {
    return userStreamSearchRepository.findAttendedByUser(member.getMemberId(), pageable);
  }

  @Override
  public Page<FleenStream> findAttendedByDateBetweenAndUser(
    final LocalDateTime startDate, final LocalDateTime endDate, final IsAMember member, final Pageable pageable) {
    return userStreamSearchRepository.findAttendedByDateBetweenAndUser(startDate, endDate, member.getMemberId(), pageable);
  }

  @Override
  public Page<FleenStream> findAttendedByTitleAndUser(final String title, final IsAMember member, final Pageable pageable) {
    return userStreamSearchRepository.findAttendedByTitleAndUser(title, member.getMemberId(), pageable);
  }

  @Override
  public Page<FleenStream> findStreamsAttendedTogether(final IsAMember you, final IsAMember friend, final Pageable pageable) {
    return userStreamSearchRepository.findStreamsAttendedTogether(you.getMemberId(), friend.getMemberId(), pageable);
  }

  @Override
  public FleenStream findStream(final Long streamId) throws StreamNotFoundException {
    return streamService.findStream(streamId);
  }

  @Override
  public FleenStream save(final FleenStream stream) {
    return streamManagementRepository.save(stream);
  }

  @Override
  public Optional<FleenStream> findById(final Long streamId) {
    return streamManagementRepository.findById(streamId);
  }
}

