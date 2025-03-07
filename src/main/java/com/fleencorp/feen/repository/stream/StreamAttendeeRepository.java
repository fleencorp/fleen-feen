package com.fleencorp.feen.repository.stream;

import com.fleencorp.feen.constant.stream.StreamType;
import com.fleencorp.feen.constant.stream.attendee.StreamAttendeeRequestToJoinStatus;
import com.fleencorp.feen.model.domain.stream.FleenStream;
import com.fleencorp.feen.model.domain.stream.StreamAttendee;
import com.fleencorp.feen.model.domain.user.Member;
import com.fleencorp.feen.model.projection.stream.attendee.StreamAttendeeInfoSelect;
import com.fleencorp.feen.model.projection.stream.attendee.StreamAttendeeSelect;
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

  @Query(value = "SELECT sa FROM StreamAttendee sa WHERE sa.stream = :stream AND sa.member = :member")
  Optional<StreamAttendee> findOrganizerByStream(FleenStream stream, Member member);

  @Query("SELECT DISTINCT sa FROM StreamAttendee sa WHERE sa.member.emailAddress = :emailAddress")
  Optional<StreamAttendee> findDistinctByEmail(@Param("emailAddress") String emailAddress);

  @Query("SELECT sa FROM StreamAttendee sa WHERE sa.attendeeId IN (:attendeeIds)")
  List<StreamAttendee> findAllByAttendeeIds(@Param("attendeeIds") Set<Long> attendeeIds);

  List<StreamAttendee> findAllByStreamAndRequestToJoinStatus(FleenStream stream, StreamAttendeeRequestToJoinStatus requestToJoinStatus);

  @Query(value = "SELECT sa FROM StreamAttendee sa WHERE sa.stream = :stream AND sa.stream.streamType = :streamType")
  Page<StreamAttendee> findByStreamAndStreamType(FleenStream stream, StreamType streamType, Pageable pageable);

  @Query(value = "SELECT sa FROM StreamAttendee sa WHERE sa.stream = :stream AND sa.requestToJoinStatus IN (:requestToJoinStatuses)")
  Page<StreamAttendee> findByStreamAndRequestToJoinStatus(FleenStream stream, @Param("requestToJoinStatuses") Set<StreamAttendeeRequestToJoinStatus> requestToJoinStatuses, Pageable pageable);

  @Query("SELECT sa FROM StreamAttendee sa WHERE sa.stream = :stream AND sa.attending = true")
  Page<StreamAttendee> findAttendeesGoingToStream(@Param("stream") FleenStream stream, Pageable pageable);

  Page<StreamAttendee> findAllByStreamAndRequestToJoinStatusAndAttending(FleenStream stream, StreamAttendeeRequestToJoinStatus requestToJoinStatus, Boolean isAttending, Pageable pageable);

  @Query("SELECT new com.fleencorp.feen.model.projection.stream.attendee.StreamAttendeeInfoSelect(sa.attendeeId, fs.streamId, m.firstName, m.lastName, m.username) " +
    "FROM StreamAttendee sa LEFT JOIN sa.stream fs LEFT JOIN sa.member m WHERE (fs.streamId = :streamId AND sa.aSpeaker = false) " +
    "AND (fs.organizerId = :organizerId) AND (m.username = :q OR m.firstName = :q OR m.lastName = :q) ")
  Page<StreamAttendeeInfoSelect> findPotentialAttendeeSpeakersByStreamAndFullNameOrUsername(
    @Param("streamId") Long streamId, @Param("organizerId") Long organizerId, @Param("q") String userIdOrName, Pageable pageable);

  @Query("SELECT new com.fleencorp.feen.model.projection.stream.attendee.StreamAttendeeSelect(fs.streamId, sa.requestToJoinStatus, sa.attending, sa.aSpeaker, sa.stream.streamVisibility, sa.stream.scheduledEndDate) " +
    "FROM StreamAttendee sa LEFT JOIN sa.member m LEFT JOIN sa.stream fs WHERE m = :member AND fs.streamId IN (:ids)")
  List<StreamAttendeeSelect> findByMemberAndStreamIds(Member member, @Param("ids") List<Long> streamIds);

  @Query("SELECT sa FROM StreamAttendee sa WHERE sa.stream = :stream AND sa.member = :member")
  Optional<StreamAttendee> findAttendeeByStreamAndUser(@Param("stream") FleenStream stream, @Param("member") Member member);

  @Query("SELECT sa FROM StreamAttendee sa WHERE sa.attendeeId = :attendeeId AND sa.stream = :stream")
  Optional<StreamAttendee> findAttendeeByIdAndStream(@Param("attendeeId") Long attendeeId, @Param("stream") FleenStream stream);

  @Query("SELECT sa FROM StreamAttendee sa WHERE sa.stream = :stream AND sa.attending = true")
  Set<StreamAttendee> findAttendeesGoingToStream(@Param("stream") FleenStream stream);

  @Query("SELECT sa FROM StreamAttendee sa WHERE sa.attendeeId IN (:attendeeIds) AND sa.streamId = :streamId AND sa.requestToJoinStatus IN (:statuses)")
  Set<StreamAttendee> findAttendeesByIdsAndStreamIdAndStatuses(@Param("attendeeIds") List<Long> speakerAttendeeIds, @Param("streamId") Long streamId, @Param("statuses") List<StreamAttendeeRequestToJoinStatus> statuses);

  long countByStreamAndRequestToJoinStatusAndAttending(FleenStream stream, StreamAttendeeRequestToJoinStatus requestToJoinStatus, Boolean isAttending);

  @Query("SELECT COUNT(sa.attendeeId) FROM StreamAttendee sa WHERE sa.attendeeId IN (:ids)")
  long countByIds(@Param("ids") Set<Long> ids);

  @Modifying
  @Query("UPDATE StreamAttendee SET requestToJoinStatus = :status WHERE attendeeId IN (:attendeeIds)")
  void approveAllAttendeeRequestInvitation(@Param("status") StreamAttendeeRequestToJoinStatus status, List<Long> attendeeIds);

  @Modifying
  @Query("UPDATE StreamAttendee SET aSpeaker = true WHERE attendeeId IN (:attendeeIds)")
  void markAllAttendeesAsSpeaker(List<Long> attendeeIds);
}
