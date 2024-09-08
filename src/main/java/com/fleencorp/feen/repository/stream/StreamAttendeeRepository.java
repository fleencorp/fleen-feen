package com.fleencorp.feen.repository.stream;

import com.fleencorp.feen.constant.stream.StreamAttendeeRequestToJoinStatus;
import com.fleencorp.feen.model.domain.stream.FleenStream;
import com.fleencorp.feen.model.domain.stream.StreamAttendee;
import com.fleencorp.feen.model.domain.user.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface StreamAttendeeRepository extends JpaRepository<StreamAttendee, Long> {

  @Query("SELECT DISTINCT sa FROM StreamAttendee sa WHERE sa.member.emailAddress = :emailAddress")
  Optional<StreamAttendee> findDistinctByEmail(@Param("emailAddress") String emailAddress);

  List<StreamAttendee> findAllByFleenStreamAndStreamAttendeeRequestToJoinStatus(FleenStream fleenStream, StreamAttendeeRequestToJoinStatus requestToJoinStatus);

  @Modifying
  @Query("UPDATE StreamAttendee SET streamAttendeeRequestToJoinStatus = :status WHERE member.memberId IN (:userIds)")
  void approveAllAttendeeRequestInvitation(@Param("status") StreamAttendeeRequestToJoinStatus status, Set<Long> userIds);

  Page<StreamAttendee> findByFleenStream(FleenStream fleenStream, Pageable pageable);

  Page<StreamAttendee> findByFleenStreamAndStreamAttendeeRequestToJoinStatus(FleenStream fleenStream, StreamAttendeeRequestToJoinStatus requestToJoinStatus, Pageable pageable);

  Optional<StreamAttendee> findByFleenStreamAndMember(FleenStream fleenStream, Member member);

  Page<StreamAttendee> findAllByFleenStreamAndStreamAttendeeRequestToJoinStatusAndIsAttending(FleenStream fleenStream, StreamAttendeeRequestToJoinStatus requestToJoinStatus, Boolean isAttending, Pageable pageable);

  long countByFleenStreamAndStreamAttendeeRequestToJoinStatusAndIsAttending(FleenStream fleenStream, StreamAttendeeRequestToJoinStatus requestToJoinStatus, Boolean isAttending);
}
