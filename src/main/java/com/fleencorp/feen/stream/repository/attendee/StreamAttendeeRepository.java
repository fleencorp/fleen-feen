package com.fleencorp.feen.stream.repository.attendee;

import com.fleencorp.feen.shared.stream.contract.IsAttendee;
import com.fleencorp.feen.stream.constant.attendee.StreamAttendeeRequestToJoinStatus;
import com.fleencorp.feen.stream.constant.core.StreamType;
import com.fleencorp.feen.stream.model.domain.StreamAttendee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface StreamAttendeeRepository extends JpaRepository<StreamAttendee, Long> {

  @Query(
    value = """
          SELECT 
              sa.attendee_id AS attendeeId,
              sa.stream_id AS streamId,
              sa.member_id AS memberId,
              sa.attending AS attending,
              sa.a_speaker AS aSpeaker,
              sa.is_organizer AS isOrganizer,
              sa.attendee_comment AS attendeeComment,
              sa.organizer_comment AS organizerComment,
              sa.request_to_join_status AS requestToJoinStatus,
              m.email_address AS emailAddress,
              m.full_name AS fullName,
              m.username AS username,
              m.profile_photo AS profilePhoto
          FROM stream_attendee sa
          JOIN fleen_stream s ON sa.stream_id = s.stream_id
          JOIN member m ON sa.member_id = m.member_id
          WHERE sa.stream_id = :#{#stream.streamId}
            AND s.stream_type = :streamType
          """,
    countQuery = """
          SELECT COUNT(*)
          FROM stream_attendee sa
          JOIN fleen_stream s ON sa.stream_id = s.stream_id
          WHERE sa.stream_id = :streamId
            AND s.stream_type = :streamType
          """,
    nativeQuery = true
  )
  Page<IsAttendee> findByStreamAndStreamType(
    @Param("streamId") Long streamId,
    @Param("streamType") StreamType streamType,
    Pageable pageable
  );

  @Query(
    value = """
          SELECT 
              sa.attendee_id AS attendeeId,
              sa.stream_id AS streamId,
              sa.member_id AS memberId,
              sa.attending AS attending,
              sa.a_speaker AS aSpeaker,
              sa.is_organizer AS isOrganizer,
              sa.attendee_comment AS attendeeComment,
              sa.organizer_comment AS organizerComment,
              sa.request_to_join_status AS requestToJoinStatus,
              m.email_address AS emailAddress,
              m.full_name AS fullName,
              m.username AS username,
              m.profile_photo AS profilePhoto
          FROM stream_attendee sa
          JOIN member m ON sa.member_id = m.member_id
          WHERE sa.stream_id = :streamId
            AND sa.request_to_join_status IN (:requestToJoinStatuses)
          """,
    countQuery = """
          SELECT COUNT(*)
          FROM stream_attendee sa
          WHERE sa.stream_id = :streamId
            AND sa.request_to_join_status IN (:requestToJoinStatuses)
          """,
    nativeQuery = true
  )
  Page<IsAttendee> findByStreamAndRequestToJoinStatus(
    @Param("streamId") Long streamId,
    @Param("requestToJoinStatuses") Set<StreamAttendeeRequestToJoinStatus> requestToJoinStatuses,
    Pageable pageable
  );

  @Query(
    value = """
          SELECT 
              sa.attendee_id AS attendeeId,
              sa.stream_id AS streamId,
              sa.member_id AS memberId,
              sa.attending AS attending,
              sa.a_speaker AS aSpeaker,
              sa.is_organizer AS isOrganizer,
              sa.attendee_comment AS attendeeComment,
              sa.organizer_comment AS organizerComment,
              sa.request_to_join_status AS requestToJoinStatus,
              m.email_address AS emailAddress,
              m.full_name AS fullName,
              m.username AS username,
              m.profile_photo AS profilePhoto
          FROM stream_attendee sa
          JOIN member m ON sa.member_id = m.member_id
          WHERE sa.stream_id = :#{#stream.streamId}
            AND sa.attending = true
          """,
    countQuery = """
          SELECT COUNT(*)
          FROM stream_attendee sa
          WHERE sa.stream_id = :streamId
            AND sa.attending = true
          """,
    nativeQuery = true
  )
  Page<IsAttendee> findAttendeesGoingToStream(
    @Param("streamId") Long streamId,
    Pageable pageable
  );

  @Query(
    value = """
          SELECT 
              sa.stream_attendee_id AS attendeeId,
              sa.stream_id AS streamId,
              sa.member_id AS memberId,
              sa.attending AS attending,
              sa.is_a_speaker AS aSpeaker,
              sa.is_organizer AS isOrganizer,
              sa.attendee_comment AS attendeeComment,
              sa.organizer_comment AS organizerComment,
              sa.request_to_join_status AS requestToJoinStatus,
              m.email_address AS emailAddress,
              CONCAT(m.first_name, ' ', m.last_name) AS fullName,
              m.username AS username,
              m.profile_photo AS profilePhoto,
              m.first_name AS firstName,
              m.last_name AS lastName
          FROM stream_attendee sa
          JOIN member m ON sa.member_id = m.member_id
          WHERE sa.stream_id = :streamId
            AND sa.request_to_join_status = :requestToJoinStatus
            AND sa.is_attending = :isAttending
          """,
    countQuery = """
          SELECT COUNT(*)
          FROM stream_attendee sa
          WHERE s.stream_id = :streamId
            AND sa.request_to_join_status = :requestToJoinStatus
            AND sa.is_attending = :isAttending
          """,
    nativeQuery = true
  )
  Page<IsAttendee> findAllByStreamAndRequestToJoinStatusAndAttending(
    @Param("streamId") Long streamId,
    @Param("requestToJoinStatus") StreamAttendeeRequestToJoinStatus requestToJoinStatus,
    @Param("isAttending") Boolean isAttending,
    Pageable pageable
  );
}
