package com.fleencorp.feen.stream.repository.speaker;

import com.fleencorp.feen.stream.model.domain.FleenStream;
import com.fleencorp.feen.stream.model.domain.StreamSpeaker;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Set;

public interface StreamSpeakerRepository extends JpaRepository<StreamSpeaker, Long> {

  @Query("SELECT ss FROM StreamSpeaker ss WHERE ss.speakerId IN (:ids)")
  Set<StreamSpeaker> findAllByIds(@Param("ids") Set<Long> speakersIds);

  @Query("SELECT ss FROM StreamSpeaker ss WHERE ss.stream = :stream")
  Page<StreamSpeaker> findAllByStream(@Param("stream") FleenStream stream, Pageable pageable);

  Optional<StreamSpeaker> findBySpeakerIdAndStream(StreamSpeaker speaker, FleenStream stream);

}
