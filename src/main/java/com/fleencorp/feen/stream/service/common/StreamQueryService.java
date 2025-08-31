package com.fleencorp.feen.stream.service.common;

import com.fleencorp.feen.stream.constant.core.StreamStatus;
import com.fleencorp.feen.stream.constant.core.StreamType;
import com.fleencorp.feen.stream.constant.core.StreamVisibility;
import com.fleencorp.feen.stream.exception.core.StreamNotFoundException;
import com.fleencorp.feen.stream.model.domain.FleenStream;
import com.fleencorp.feen.user.model.domain.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Optional;

public interface StreamQueryService {

  Page<FleenStream> findByDateBetween(LocalDateTime startDate, LocalDateTime endDate, StreamStatus status, Pageable pageable);

  Page<FleenStream> findByTitle(String title, StreamStatus status, Pageable pageable);

  Page<FleenStream> findMany(StreamStatus status, Pageable pageable);

  Page<FleenStream> findUpcomingStreams(LocalDateTime dateTime, StreamType streamType, Pageable pageable);

  Page<FleenStream> findUpcomingStreamsByTitle(String title, LocalDateTime dateTime, StreamType streamType, Pageable pageable);

  Page<FleenStream> findPastStreams(LocalDateTime dateTime, StreamType streamType, Pageable pageable);

  Page<FleenStream> findPastStreamsByTitle(String title, LocalDateTime dateTime, StreamType streamType, Pageable pageable);

  Page<FleenStream> findLiveStreams(LocalDateTime dateTime, StreamType streamType, Pageable pageable);

  Page<FleenStream> findLiveStreamsByTitle(String title, LocalDateTime dateTime, StreamType streamType, Pageable pageable);

  Page<FleenStream> findManyByMe(Member member, Pageable pageable);

  Page<FleenStream> findByTitleAndUser(String title, Member member, Pageable pageable);

  Page<FleenStream> findByTitleAndUser(String title, StreamVisibility streamVisibility, Member member, Pageable pageable);

  Page<FleenStream> findByDateBetweenAndUser(LocalDateTime startDate, LocalDateTime endDate, Member member, Pageable pageable);

  Page<FleenStream> findByDateBetweenAndUser(LocalDateTime startDate, LocalDateTime endDate, StreamVisibility streamVisibility, Member member, Pageable pageable);

  Page<FleenStream> findAttendedByUser(Member member, Pageable pageable);

  Page<FleenStream> findAttendedByDateBetweenAndUser(LocalDateTime startDate, LocalDateTime endDate, Member member, Pageable pageable);

  Page<FleenStream> findAttendedByTitleAndUser(String title, Member member, Pageable pageable);

  Page<FleenStream> findStreamsAttendedTogether(Member you, Member friend, Pageable pageable);

  FleenStream findStream(Long streamId) throws StreamNotFoundException;

  FleenStream save(FleenStream stream);

  Optional<FleenStream> findById(Long streamId);
}
