package com.fleencorp.feen.repository.stream;

import com.fleencorp.feen.constant.stream.StreamAttendeeRequestToJoinStatus;
import com.fleencorp.feen.model.domain.stream.FleenStream;
import com.fleencorp.feen.model.domain.stream.StreamAttendee;
import com.fleencorp.feen.model.domain.user.Member;
import com.fleencorp.feen.model.projection.StreamAttendeeSelect;
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
  void approveAllAttendeeRequestInvitation(@Param("status") StreamAttendeeRequestToJoinStatus status, List<Long> userIds);

  Page<StreamAttendee> findByFleenStream(FleenStream fleenStream, Pageable pageable);

  Page<StreamAttendee> findByFleenStreamAndStreamAttendeeRequestToJoinStatus(FleenStream fleenStream, StreamAttendeeRequestToJoinStatus requestToJoinStatus, Pageable pageable);

  Optional<StreamAttendee> findByFleenStreamAndMember(FleenStream fleenStream, Member member);

  Page<StreamAttendee> findAllByFleenStreamAndStreamAttendeeRequestToJoinStatusAndIsAttending(FleenStream fleenStream, StreamAttendeeRequestToJoinStatus requestToJoinStatus, Boolean isAttending, Pageable pageable);

  long countByFleenStreamAndStreamAttendeeRequestToJoinStatusAndIsAttending(FleenStream fleenStream, StreamAttendeeRequestToJoinStatus requestToJoinStatus, Boolean isAttending);

  @Query("SELECT sa FROM StreamAttendee sa WHERE sa.fleenStream.fleenStreamId = :eventOrStreamId AND sa.member.memberId IN (:memberIds) AND sa.streamAttendeeRequestToJoinStatus IN (:statuses)")
  Set<StreamAttendee> findAttendeesByEventOrStreamIdAndMemberIdsAndStatuses(@Param("eventOrStreamId") Long eventOrStreamId, @Param("memberIds") List<Long> speakerMemberIds, @Param("statuses") List<StreamAttendeeRequestToJoinStatus> statuses);

  @Query("SELECT new com.fleencorp.feen.model.projection.StreamAttendeeSelect(fs.fleenStreamId, sa.streamAttendeeRequestToJoinStatus) " +
    "FROM StreamAttendee sa LEFT JOIN sa.member m LEFT JOIN sa.fleenStream fs WHERE m = :member AND fs.fleenStreamId IN (:ids)")
  List<StreamAttendeeSelect> findByMemberAndEventOrStreamIds(Member member, @Param("ids") List<Long> eventOrStreamIds);
}
