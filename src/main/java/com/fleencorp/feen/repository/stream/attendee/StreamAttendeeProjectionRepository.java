package com.fleencorp.feen.repository.stream.attendee;

import com.fleencorp.feen.model.domain.stream.StreamAttendee;
import com.fleencorp.feen.user.model.domain.Member;
import com.fleencorp.feen.model.projection.stream.attendee.StreamAttendeeInfoSelect;
import com.fleencorp.feen.model.projection.stream.attendee.StreamAttendeeSelect;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StreamAttendeeProjectionRepository extends JpaRepository<StreamAttendee, Long> {

  /**
   * Finds non-speaker attendees of a specific stream, organized by the given organizer,
   * whose username, first name, or last name matches the provided query string.
   *
   * <p>This method is typically used to search for potential attendees who can be selected
   * or invited as speakers. The results are paginated and returned as {@link StreamAttendeeInfoSelect}
   * projections.</p>
   *
   * @param streamId the ID of the stream to search within
   * @param organizerId the ID of the stream organizer to ensure authorized access
   * @param userIdOrName a query string used to match against the attendee's username, first name, or last name
   * @param pageable the pagination configuration
   * @return a {@link Page} of {@link StreamAttendeeInfoSelect} matching the search criteria
   */
  @Query(value =
    """
      SELECT new com.fleencorp.feen.model.projection.stream.attendee.StreamAttendeeInfoSelect(sa.attendeeId, fs.streamId, m.firstName, m.lastName, m.username)
      FROM StreamAttendee sa
      LEFT JOIN sa.stream fs
      LEFT JOIN sa.member m
      WHERE (fs.streamId = :streamId AND sa.aSpeaker = false)
        AND (fs.organizerId = :organizerId) 
        AND (m.username = :q OR m.firstName = :q OR m.lastName = :q)
    """)
  Page<StreamAttendeeInfoSelect> findPotentialAttendeeSpeakersByStreamAndFullNameOrUsername(
    @Param("streamId") Long streamId, @Param("organizerId") Long organizerId, @Param("q") String userIdOrName, Pageable pageable);

  /**
   * Retrieves a list of stream attendee information for a given member and a list of stream IDs.
   * Each result includes the member’s request-to-join status, attendance status, speaker status,
   * stream visibility, scheduled end date, and whether the member liked the stream.
   *
   * <p>The result is returned as {@link StreamAttendeeSelect} projections using a JPQL query
   * with a conditional aggregate to determine if a "LIKE" exists by the member on each stream.</p>
   *
   * @param member the {@link Member} whose attendance data is to be fetched
   * @param streamIds the list of stream IDs to filter the attendance records
   * @return a list of {@link StreamAttendeeSelect} projections containing attendance and like info for the member
   */
  @Query(value =
    """
      SELECT new com.fleencorp.feen.model.projection.stream.attendee.StreamAttendeeSelect(
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
      WHERE m = :member
      AND fs.streamId IN (:streamIds)
      GROUP BY fs.streamId, sa.requestToJoinStatus, sa.attending, sa.aSpeaker, sa.stream.streamVisibility, sa.stream.scheduledEndDate
    """)
  List<StreamAttendeeSelect> findByMemberAndStreamIds(Member member, @Param("streamIds") List<Long> streamIds);
}
