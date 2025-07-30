package com.fleencorp.feen.chat.space.repository.member;

import com.fleencorp.feen.chat.space.model.domain.ChatSpaceMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChatSpaceMemberParticipationRepository extends JpaRepository<ChatSpaceMember, Long> {

  /**
   * Checks whether both the viewer and the target member are members of the same chat space.
   *
   * @param memberId the ID of the viewer (member initiating the action)
   * @param targetMemberId the ID of the target member being evaluated
   * @return {@code true} if both members are in the specified chat space, {@code false} otherwise
   */
  @Query("""
        SELECT COUNT(csm) > 0
        FROM ChatSpaceMember csm
        WHERE csm.chatSpaceId IS NOT NULL
        AND csm.memberId IN (:memberId, :targetMemberId)
        GROUP BY csm.chatSpace.chatSpaceId
        HAVING COUNT(csm.memberId) = 2
    """)
  Boolean existsByMembers(@Param("memberId") Long memberId, @Param("targetMemberId") Long targetMemberId);

}
