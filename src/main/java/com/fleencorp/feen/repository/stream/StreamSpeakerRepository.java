package com.fleencorp.feen.repository.stream;

import com.fleencorp.feen.model.domain.stream.FleenStream;
import com.fleencorp.feen.model.domain.stream.StreamSpeaker;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface StreamSpeakerRepository extends JpaRepository<StreamSpeaker, Long> {

  Set<StreamSpeaker> findAllByFleenStream(FleenStream stream);
}
