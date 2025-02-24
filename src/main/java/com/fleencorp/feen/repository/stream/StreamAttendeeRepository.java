package com.fleencorp.feen.repository.stream;

import com.fleencorp.feen.constant.stream.StreamType;
import com.fleencorp.feen.constant.stream.attendee.StreamAttendeeRequestToJoinStatus;
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

  List<StreamAttendee> findAllByStreamAndRequestToJoinStatus(FleenStream stream, StreamAttendeeRequestToJoinStatus requestToJoinStatus);

  @Modifying
  @Query("UPDATE StreamAttendee SET requestToJoinStatus = :status WHERE member.memberId IN (:userIds)")
  void approveAllAttendeeRequestInvitation(@Param("status") StreamAttendeeRequestToJoinStatus status, List<Long> userIds);

  @Query(value = "SELECT sa FROM StreamAttendee sa WHERE sa.stream = :stream AND sa.stream.streamType = :streamType")
  Page<StreamAttendee> findByStreamAndStreamType(FleenStream stream, StreamType streamType, Pageable pageable);

  @Query(value = "SELECT sa FROM StreamAttendee sa WHERE sa.stream = :stream AND sa.requestToJoinStatus IN (:requestToJoinStatuses)")
  Page<StreamAttendee> findByStreamAndRequestToJoinStatus(FleenStream stream, @Param("requestToJoinStatuses") Set<StreamAttendeeRequestToJoinStatus> requestToJoinStatuses, Pageable pageable);

  Page<StreamAttendee> findAllByStreamAndRequestToJoinStatusAndAttending(FleenStream stream, StreamAttendeeRequestToJoinStatus requestToJoinStatus, Boolean isAttending, Pageable pageable);

  long countByStreamAndRequestToJoinStatusAndAttending(FleenStream stream, StreamAttendeeRequestToJoinStatus requestToJoinStatus, Boolean isAttending);

  @Query("SELECT sa FROM StreamAttendee sa WHERE sa.stream.streamId = :streamId AND sa.member.memberId IN (:memberIds) AND sa.requestToJoinStatus IN (:statuses)")
  Set<StreamAttendee> findAttendeesByEventOrStreamIdAndMemberIdsAndStatuses(@Param("streamId") Long streamId, @Param("memberIds") List<Long> speakerMemberIds, @Param("statuses") List<StreamAttendeeRequestToJoinStatus> statuses);

  @Query("SELECT new com.fleencorp.feen.model.projection.StreamAttendeeSelect(fs.streamId, sa.requestToJoinStatus, sa.attending, sa.stream.streamVisibility, sa.stream.scheduledEndDate) " +
    "FROM StreamAttendee sa LEFT JOIN sa.member m LEFT JOIN sa.stream fs WHERE m = :member AND fs.streamId IN (:ids)")
  List<StreamAttendeeSelect> findByMemberAndStreamIds(Member member, @Param("ids") List<Long> streamIds);

  @Query("SELECT sa FROM StreamAttendee sa WHERE sa.stream = :stream AND sa.member = :member")
  Optional<StreamAttendee> findAttendeeByStreamAndUser(@Param("stream") FleenStream stream, @Param("member") Member member);

  @Query("SELECT sa FROM StreamAttendee sa WHERE sa.streamAttendeeId = :attendeeId AND sa.stream = :stream")
  Optional<StreamAttendee> findAttendeeByIdAndStream(@Param("attendeeId") Long attendeeId, @Param("stream") FleenStream stream);

  @Query("SELECT sa FROM StreamAttendee sa WHERE sa.stream = :stream AND sa.attending = true")
  Set<StreamAttendee> findAttendeesGoingToStream(@Param("stream") FleenStream stream);

  @Query("SELECT sa FROM StreamAttendee sa WHERE sa.stream = :stream AND sa.attending = true")
  Page<StreamAttendee> findAttendeesGoingToStream(@Param("stream") FleenStream stream, Pageable pageable);
}
