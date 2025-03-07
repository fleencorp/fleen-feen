package com.fleencorp.feen.repository.stream;

import com.fleencorp.feen.model.domain.stream.FleenStream;
import com.fleencorp.feen.model.domain.stream.StreamSpeaker;
import com.fleencorp.feen.model.domain.user.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface StreamSpeakerRepository extends JpaRepository<StreamSpeaker, Long> {

  @Query("SELECT ss FROM StreamSpeaker ss WHERE ss.stream = :stream AND ss.member = :member")
  Page<StreamSpeaker> findAllByStreamExcludingOrganizer(@Param("stream") FleenStream stream, Member member, Pageable pageable);

  Optional<StreamSpeaker> findBySpeakerIdAndStream(StreamSpeaker speaker, FleenStream stream);

}
