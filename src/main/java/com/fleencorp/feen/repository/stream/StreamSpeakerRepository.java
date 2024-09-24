package com.fleencorp.feen.repository.stream;

import com.fleencorp.feen.model.domain.stream.FleenStream;
import com.fleencorp.feen.model.domain.stream.StreamSpeaker;
import com.fleencorp.feen.model.domain.user.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;

public interface StreamSpeakerRepository extends JpaRepository<StreamSpeaker, Long> {

  Set<StreamSpeaker> findAllByFleenStream(FleenStream stream);

  Optional<StreamSpeaker> findByFleenStreamAndMember(FleenStream stream, Member member);

}
