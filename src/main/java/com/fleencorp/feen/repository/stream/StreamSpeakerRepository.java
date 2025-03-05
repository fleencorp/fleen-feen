package com.fleencorp.feen.repository.stream;

import com.fleencorp.feen.model.domain.stream.FleenStream;
import com.fleencorp.feen.model.domain.stream.StreamSpeaker;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StreamSpeakerRepository extends JpaRepository<StreamSpeaker, Long> {

  Page<StreamSpeaker> findAllByStream(FleenStream stream, Pageable pageable);

  Optional<StreamSpeaker> findBySpeakerIdAndStream(StreamSpeaker speaker, FleenStream stream);

}
