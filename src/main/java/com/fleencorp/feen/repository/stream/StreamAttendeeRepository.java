package com.fleencorp.feen.repository.stream;

import com.fleencorp.feen.constant.stream.StreamAttendeeRequestToJoinStatus;
import com.fleencorp.feen.model.domain.stream.FleenStream;
import com.fleencorp.feen.model.domain.stream.StreamAttendee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StreamAttendeeRepository extends JpaRepository<StreamAttendee, Long> {

  @Query("SELECT DISTINCT sa FROM StreamAttendee sa WHERE sa.member.emailAddress = :emailAddress")
  Optional<StreamAttendee> findDistinctByEmail(@Param("emailAddress") String emailAddress);

  List<StreamAttendee> findAllByFleenStreamAndStreamAttendeeRequestToJoinStatus(FleenStream fleenStream, StreamAttendeeRequestToJoinStatus requestToJoinStatus);
}
