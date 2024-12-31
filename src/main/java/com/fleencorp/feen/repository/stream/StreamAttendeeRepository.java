package com.fleencorp.feen.repository.stream;

import com.fleencorp.feen.constant.stream.StreamAttendeeRequestToJoinStatus;
import com.fleencorp.feen.constant.stream.StreamType;
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

  List<StreamAttendee> findAllByFleenStreamAndRequestToJoinStatus(FleenStream fleenStream, StreamAttendeeRequestToJoinStatus requestToJoinStatus);

  @Modifying
  @Query("UPDATE StreamAttendee SET requestToJoinStatus = :status WHERE member.memberId IN (:userIds)")
  void approveAllAttendeeRequestInvitation(@Param("status") StreamAttendeeRequestToJoinStatus status, List<Long> userIds);

  Page<StreamAttendee> findByFleenStream(FleenStream fleenStream, Pageable pageable);

  @Query(value = "SELECT sa FROM StreamAttendee sa WHERE sa.fleenStream = :fleenStream AND sa.fleenStream.streamType = :streamType")
  Page<StreamAttendee> findByFleenStreamAndStreamType(FleenStream fleenStream, StreamType streamType, Pageable pageable);

  Page<StreamAttendee> findByFleenStreamAndRequestToJoinStatus(FleenStream fleenStream, StreamAttendeeRequestToJoinStatus requestToJoinStatus, Pageable pageable);

  Page<StreamAttendee> findAllByFleenStreamAndRequestToJoinStatusAndAttending(FleenStream fleenStream, StreamAttendeeRequestToJoinStatus requestToJoinStatus, Boolean isAttending, Pageable pageable);

  long countByFleenStreamAndRequestToJoinStatusAndAttending(FleenStream fleenStream, StreamAttendeeRequestToJoinStatus requestToJoinStatus, Boolean isAttending);

  @Query("SELECT sa FROM StreamAttendee sa WHERE sa.fleenStream.fleenStreamId = :eventOrStreamId AND sa.member.memberId IN (:memberIds) AND sa.requestToJoinStatus IN (:statuses)")
  Set<StreamAttendee> findAttendeesByEventOrStreamIdAndMemberIdsAndStatuses(@Param("eventOrStreamId") Long eventOrStreamId, @Param("memberIds") List<Long> speakerMemberIds, @Param("statuses") List<StreamAttendeeRequestToJoinStatus> statuses);

  @Query("SELECT new com.fleencorp.feen.model.projection.StreamAttendeeSelect(fs.fleenStreamId, sa.requestToJoinStatus, sa.attending, sa.fleenStream.streamVisibility, sa.fleenStream.scheduledEndDate) " +
    "FROM StreamAttendee sa LEFT JOIN sa.member m LEFT JOIN sa.fleenStream fs WHERE m = :member AND fs.fleenStreamId IN (:ids)")
  List<StreamAttendeeSelect> findByMemberAndStreamIds(Member member, @Param("ids") List<Long> eventOrStreamIds);

  @Query("SELECT sa FROM StreamAttendee sa WHERE sa.fleenStream = :fleenStream AND sa.member = :member")
  Optional<StreamAttendee> findAttendeeByStreamAndUser(@Param("fleenStream") FleenStream fleenStream, @Param("member") Member member);

  @Query("SELECT sa FROM StreamAttendee sa WHERE sa.fleenStream = :fleenStream AND sa.attending = true")
  Set<StreamAttendee> findAttendeesGoingToStream(@Param("fleenStream") FleenStream fleenStream);
}
