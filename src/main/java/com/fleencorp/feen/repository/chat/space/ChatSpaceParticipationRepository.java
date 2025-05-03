package com.fleencorp.feen.repository.chat.space;

import com.fleencorp.feen.constant.chat.space.ChatSpaceRequestToJoinStatus;
import com.fleencorp.feen.constant.chat.space.ChatSpaceStatus;
import com.fleencorp.feen.model.domain.chat.ChatSpace;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;

public interface ChatSpaceParticipationRepository extends JpaRepository<ChatSpace, Long> {

  /**
   * Finds active chat spaces that are common to both specified members.
   *
   * <p>This query returns chat spaces that are not deleted and have an active
   * status as specified by {@code activeStatuses}. It includes only those chat
   * spaces where both {@code memberAId} and {@code memberBId} are approved,
   * have not left, and have not been removed from the chat space.</p>
   *
   * <p>The result is ordered by the chat space title in ascending order and
   * returned as a pageable result.</p>
   *
   * @param memberAId The ID of the first member.
   * @param memberBId The ID of the second member.
   * @param approvedStatuses The statuses that indicate a member was approved to join.
   * @param activeStatuses The chat space statuses to include in the result.
   * @param pageable The pagination and sorting configuration.
   * @return A page of {@link ChatSpace} instances common to both members.
   */
  @Query(value =
    """
    SELECT cs FROM ChatSpace cs
    WHERE cs.deleted = false
      AND cs.status IN :activeStatuses
      AND EXISTS (
        SELECT 1 FROM ChatSpaceMember csm1
        WHERE csm1.chatSpace = cs
          AND csm1.member.memberId = :memberAId
          AND csm1.requestToJoinStatus IN (:approvedStatuses)
          AND csm1.left = false
          AND csm1.removed = false
      )
      AND EXISTS (
        SELECT 1 FROM ChatSpaceMember csm2
        WHERE csm2.chatSpace = cs
          AND csm2.member.memberId = :memberBId
          AND csm2.requestToJoinStatus IN (:approvedStatuses)
          AND csm2.left = false
          AND csm2.removed = false
      )
    ORDER BY cs.title ASC
    """)
  Page<ChatSpace> findCommonChatSpaces(
    @Param("memberAId") Long memberAId,
    @Param("memberBId") Long memberBId,
    @Param("approvedStatuses") Collection<ChatSpaceRequestToJoinStatus> approvedStatuses,
    @Param("activeStatuses") Collection<ChatSpaceStatus> activeStatuses,
    Pageable pageable);


  @Query("SELECT 1 FROM ChatSpace cs JOIN cs.members m WHERE cs.chatSpaceId = :chatSpaceId AND m.memberId IN (:viewerId, :targetId)")
  boolean existsByMembers(@Param("viewerId") Long viewerId, @Param("targetId") Long targetId);
}
