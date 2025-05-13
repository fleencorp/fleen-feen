package com.fleencorp.feen.repository.stream.attendee;

import com.fleencorp.feen.constant.stream.attendee.StreamAttendeeRequestToJoinStatus;
import com.fleencorp.feen.model.domain.stream.FleenStream;
import com.fleencorp.feen.model.domain.stream.StreamAttendee;
import com.fleencorp.feen.model.domain.user.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface StreamAttendeeQueryRepository extends JpaRepository<StreamAttendee, Long> {

  @Query(value = "SELECT sa FROM StreamAttendee sa WHERE sa.stream = :stream AND sa.member = :member")
  Optional<StreamAttendee> findOrganizerByStream(FleenStream stream, Member member);

  @Query("SELECT DISTINCT sa FROM StreamAttendee sa WHERE sa.member.emailAddress = :emailAddress")
  Optional<StreamAttendee> findDistinctByEmail(@Param("emailAddress") String emailAddress);

  @Query("SELECT sa FROM StreamAttendee sa WHERE sa.attendeeId IN (:attendeeIds)")
  List<StreamAttendee> findAllByAttendeeIds(@Param("attendeeIds") Set<Long> attendeeIds);

  List<StreamAttendee> findAllByStreamAndRequestToJoinStatus(FleenStream stream, StreamAttendeeRequestToJoinStatus requestToJoinStatus);

  @Query("SELECT sa FROM StreamAttendee sa WHERE sa.stream = :stream AND sa.member = :member")
  Optional<StreamAttendee> findAttendeeByStreamAndUser(@Param("stream") FleenStream stream, @Param("member") Member member);

  @Query("SELECT sa FROM StreamAttendee sa WHERE sa.attendeeId = :attendeeId AND sa.stream = :stream")
  Optional<StreamAttendee> findAttendeeByIdAndStream(@Param("attendeeId") Long attendeeId, @Param("stream") FleenStream stream);

  @Query("SELECT sa FROM StreamAttendee sa WHERE sa.streamId = :streamId AND sa.attending = true")
  List<StreamAttendee> findAttendeesGoingToStream(@Param("streamId") Long streamId);

  @Query("SELECT sa FROM StreamAttendee sa WHERE sa.attendeeId IN (:attendeeIds) AND sa.streamId = :streamId AND sa.requestToJoinStatus IN (:statuses)")
  Set<StreamAttendee> findAttendeesByIdsAndStreamIdAndStatuses(@Param("attendeeIds") List<Long> speakerAttendeeIds, @Param("streamId") Long streamId, @Param("statuses") List<StreamAttendeeRequestToJoinStatus> statuses);
}
