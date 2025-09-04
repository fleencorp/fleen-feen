package com.fleencorp.feen.stream.repository.attendee;

import com.fleencorp.feen.shared.stream.contract.IsAttendee;
import com.fleencorp.feen.stream.constant.attendee.StreamAttendeeRequestToJoinStatus;
import com.fleencorp.feen.stream.model.domain.StreamAttendee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface StreamAttendeeSearchRepository extends JpaRepository<StreamAttendee, Long> {

  @Query(value = "SELECT sa FROM StreamAttendee sa WHERE sa.streamId = :streamId AND sa.memberId = :memberId")
  Optional<StreamAttendee> findOrganizerByStream(@Param("streamId") Long streamId, @Param("memberId") Long memberId);

  @Query(
    value = """
          SELECT sa.*
          FROM stream_attendee sa
          JOIN member m ON sa.member_id = m.member_id
          WHERE m.email_address = :emailAddress
          LIMIT 1
          """,
    nativeQuery = true
  )
  Optional<StreamAttendee> findDistinctByEmail(@Param("emailAddress") String emailAddress);

  @Query("SELECT sa FROM StreamAttendee sa WHERE sa.attendeeId IN (:attendeeIds)")
  List<StreamAttendee> findAllByAttendeeIds(@Param("attendeeIds") Set<Long> attendeeIds);

  @Query(
    value = """
          SELECT
              sa.stream_attendee_id AS attendeeId,
              sa.stream_id AS streamId,
              sa.member_id AS memberId,
              sa.is_attending AS attending,
              sa.is_a_speaker AS aSpeaker,
              sa.is_organizer AS isOrganizer,
              sa.attendee_comment AS attendeeComment,
              sa.organizer_comment AS organizerComment,
              sa.request_to_join_status AS requestToJoinStatus,
              m.email_address AS emailAddress,
              CONCAT(m.first_name, ' ', m.last_name) AS fullName,
              m.username AS username,
              m.profile_photo_url AS profilePhoto,
              m.first_name AS firstName,
              m.last_name AS lastName
          FROM stream_attendee sa
          JOIN member m ON sa.member_id = m.member_id
          WHERE sa.stream_id = :streamId
            AND sa.request_to_join_status = :requestToJoinStatus
          """,
    nativeQuery = true
  )
  List<IsAttendee> findAllByStreamAndRequestToJoinStatus(
    @Param("streamId") Long streamId,
    @Param("requestToJoinStatus") StreamAttendeeRequestToJoinStatus requestToJoinStatus
  );

  @Query("SELECT sa FROM StreamAttendee sa WHERE sa.streamId = :streamId AND sa.memberId = :memberId")
  Optional<StreamAttendee> findAttendeeByStreamAndUser(@Param("streamId") Long streamId, @Param("memberId") Long memberId);

  @Query("SELECT sa FROM StreamAttendee sa WHERE sa.attendeeId = :attendeeId AND sa.streamId = :streamId")
  Optional<StreamAttendee> findAttendeeByIdAndStream(@Param("attendeeId") Long attendeeId, @Param("streamId") Long streamId);

  @Query(
    value = """
          SELECT 
              sa.stream_attendee_id AS attendeeId,
              sa.stream_id AS streamId,
              sa.member_id AS memberId,
              sa.is_attending AS attending,
              sa.is_a_speaker AS aSpeaker,
              sa.is_organizer AS isOrganizer,
              sa.attendee_comment AS attendeeComment,
              sa.organizer_comment AS organizerComment,
              m.email_address AS emailAddress,
              CONCAT(m.first_name, ' ', m.last_name) AS fullName,
              m.username AS username,
              m.profile_photo AS profilePhoto
          FROM stream_attendee sa
          JOIN member m ON sa.member_id = m.member_id
          WHERE sa.stream_id = :streamId
            AND sa.is_attending = true
          """,
    nativeQuery = true
  )
  List<IsAttendee> findAttendeesGoingToStream(@Param("streamId") Long streamId);

  @Query("SELECT sa FROM StreamAttendee sa WHERE sa.attendeeId IN (:attendeeIds) AND sa.streamId = :streamId AND sa.requestToJoinStatus IN (:statuses)")
  Set<StreamAttendee> findAttendeesByIdsAndStreamIdAndStatuses(@Param("attendeeIds") List<Long> speakerAttendeeIds, @Param("streamId") Long streamId, @Param("statuses") List<StreamAttendeeRequestToJoinStatus> statuses);
}
