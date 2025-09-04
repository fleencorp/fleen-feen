package com.fleencorp.feen.stream.repository.attendee;

import com.fleencorp.feen.shared.stream.contract.IsAttendee;
import com.fleencorp.feen.stream.model.domain.StreamAttendee;
import com.fleencorp.feen.stream.model.projection.StreamAttendeeSelect;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StreamAttendeeProjectionRepository extends JpaRepository<StreamAttendee, Long> {

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
          JOIN stream s ON sa.stream_id = s.stream_id
          JOIN member m ON sa.member_id = m.member_id
          WHERE sa.is_a_speaker = false
            AND s.stream_id = :streamId
            AND s.member_id = :organizerId
            AND (
                  m.username = :q
                  OR m.first_name = :q
                  OR m.last_name = :q
                )
          """,
    countQuery = """
          SELECT COUNT(*)
          FROM stream_attendee sa
          JOIN stream s ON sa.stream_id = s.stream_id
          JOIN member m ON sa.member_id = m.member_id
          WHERE sa.is_a_speaker = false
            AND s.stream_id = :streamId
            AND s.member_id = :organizerId
            AND (
                  m.username = :q
                  OR m.first_name = :q
                  OR m.last_name = :q
                )
          """,
    nativeQuery = true
  )
  Page<IsAttendee> findPotentialAttendeeSpeakersByStreamAndFullNameOrUsername(
    @Param("streamId") Long streamId,
    @Param("organizerId") Long organizerId,
    @Param("q") String userIdOrName,
    Pageable pageable
  );

  /**
   * Retrieves a list of stream attendee information for a given member and a list of stream IDs.
   * Each result includes the member’s request-to-join status, attendance status, speaker status,
   * stream visibility, scheduled end date, and whether the member liked the stream.
   *
   * <p>The result is returned as {@link StreamAttendeeSelect} projections using a JPQL query
   * with a conditional aggregate to determine if a "LIKE" exists by the member on each stream.</p>
   *
   * @param memberId the {@link Long} whose attendance data is to be fetched
   * @param streamIds the list of stream IDs to filter the attendance records
   * @return a list of {@link StreamAttendeeSelect} projections containing attendance and bookmark info for the member
   */
  @Query(value =
    """
      SELECT new com.fleencorp.feen.stream.model.projection.StreamAttendeeSelect(
        fs.streamId,
        sa.requestToJoinStatus,
        sa.attending,
        sa.aSpeaker,
        sa.stream.streamVisibility,
        sa.stream.scheduledEndDate,
        CASE WHEN COUNT(l) > 0 THEN TRUE ELSE FALSE END
      )
      FROM StreamAttendee sa
      LEFT JOIN sa.member m
      LEFT JOIN sa.stream fs
      LEFT JOIN Like l
        ON l.memberId = m.memberId
        AND l.parentId = fs.streamId
        AND l.likeParentType = 'STREAM'
        AND l.likeType = 'LIKE'
      WHERE m.memberId = :memberId
      AND fs.streamId IN (:streamIds)
      GROUP BY fs.streamId, sa.requestToJoinStatus, sa.attending, sa.aSpeaker, sa.stream.streamVisibility, sa.stream.scheduledEndDate
    """)
  List<StreamAttendeeSelect> findByMemberAndStreamIds(@Param("memberId") Long memberId, @Param("streamIds") List<Long> streamIds);

}
