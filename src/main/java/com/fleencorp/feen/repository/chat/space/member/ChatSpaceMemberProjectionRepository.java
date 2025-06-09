package com.fleencorp.feen.repository.chat.space.member;

import com.fleencorp.feen.constant.chat.space.ChatSpaceRequestToJoinStatus;
import com.fleencorp.feen.model.domain.chat.ChatSpace;
import com.fleencorp.feen.model.domain.chat.ChatSpaceMember;
import com.fleencorp.feen.model.domain.like.Like;
import com.fleencorp.feen.user.model.domain.Member;
import com.fleencorp.feen.model.projection.chat.space.ChatSpaceMemberSelect;
import com.fleencorp.feen.model.projection.chat.space.ChatSpaceRequestToJoinPendingSelect;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatSpaceMemberProjectionRepository extends JpaRepository<ChatSpaceMember, Long> {

  /**
   * Retrieves a list of {@link ChatSpaceMemberSelect} projections for the specified member and a list of chat space IDs,
   * including the like status for each chat space.
   *
   * <p>This method uses a custom query to join the {@link ChatSpaceMember}, {@link Member}, {@link ChatSpace},
   * and {@link Like} entities to return a projection with chat space details and the like status of the member
   * for each chat space.</p>
   *
   * @param member the {@link Member} for whom the chat spaces and like status are to be retrieved
   * @param chatSpaceIds the list of chat space IDs for which the like status will be checked
   * @return a list of {@link ChatSpaceMemberSelect} objects containing chat space information and the like status
   */
  @Query(value =
  """
    SELECT new com.fleencorp.feen.model.projection.chat.space.ChatSpaceMemberSelect(
      cs.chatSpaceId,
      csm.requestToJoinStatus,
      csm.chatSpace.spaceVisibility,
      csm.left,
      csm.removed,
      csm.role,
      CASE WHEN COUNT(l) > 0 THEN TRUE ELSE FALSE END
    )
    FROM ChatSpaceMember csm
    LEFT JOIN csm.member m
    LEFT JOIN csm.chatSpace cs
    LEFT JOIN Like l
        ON l.memberId = m.memberId
        AND l.likeParentType = 'CHAT_SPACE'
        AND l.parentId = cs.chatSpaceId
        AND l.likeType = 'LIKE'
    WHERE m = :member
    AND cs.chatSpaceId IN (:ids)
    GROUP BY cs.chatSpaceId, csm.requestToJoinStatus, csm.chatSpace.spaceVisibility, csm.left, csm.removed, csm.role
  """)
  List<ChatSpaceMemberSelect> findByMemberAndChatSpaceIds(
    Member member,
    @Param("ids") List<Long> chatSpaceIds);

  /**
   * Counts the number of pending join requests for each chat space in the provided list.
   *
   * <p>The result is grouped by chat space ID and returned as a list of
   * {@link ChatSpaceRequestToJoinPendingSelect} projections, where each entry includes the
   * chat space ID and the corresponding count of members with the specified request-to-join status.</p>
   *
   * @param chatSpaceIds a list of chat space IDs to check for pending join requests
   * @param status the {@link ChatSpaceRequestToJoinStatus} to filter the requests (typically {@code PENDING})
   * @return a list of {@link ChatSpaceRequestToJoinPendingSelect} containing chat space IDs and their pending request counts
   */
  @Query(value =
  """
    SELECT new com.fleencorp.feen.model.projection.chat.space.ChatSpaceRequestToJoinPendingSelect(
      csm.chatSpace.chatSpaceId,
      COUNT(csm)
    )
    FROM ChatSpaceMember csm
    WHERE csm.chatSpace.chatSpaceId IN (:chatSpaceIds)
    AND csm.requestToJoinStatus = :status
    GROUP BY csm.chatSpace.chatSpaceId
  """)
  List<ChatSpaceRequestToJoinPendingSelect> countPendingJoinRequestsForChatSpaces(
    @Param("chatSpaceIds") List<Long> chatSpaceIds,
    @Param("status") ChatSpaceRequestToJoinStatus status);
}
