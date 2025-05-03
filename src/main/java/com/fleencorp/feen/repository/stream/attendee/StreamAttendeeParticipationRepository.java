package com.fleencorp.feen.repository.stream.attendee;

import com.fleencorp.feen.model.domain.stream.StreamAttendee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StreamAttendeeParticipationRepository extends JpaRepository<StreamAttendee, Long> {

  /**
   * Checks whether both the viewer and the target member have attended at least one common stream.
   *
   * @param memberId the ID of the viewer (member initiating the profile view or request)
   * @param targetMemberId the ID of the target member being viewed or evaluated
   * @return {@code true} if both members have attended at least one shared stream, {@code false} otherwise
   */
  @Query(value = """
    SELECT COUNT(sa) > 0
    FROM StreamAttendee sa
    JOIN sa.stream fs
    WHERE fs.streamId IS NOT NULL
    AND sa.memberId IN (:memberId, :targetMemberId)
    GROUP BY sa.streamId
    HAVING COUNT(sa.memberId) = 2
  """)
  boolean existsByAttendees(@Param("memberId") Long memberId, @Param("targetMemberId") Long targetMemberId);

}
