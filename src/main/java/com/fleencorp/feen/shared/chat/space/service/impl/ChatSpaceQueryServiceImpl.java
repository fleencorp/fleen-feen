package com.fleencorp.feen.shared.chat.space.service.impl;

import com.fleencorp.feen.chat.space.constant.core.ChatSpaceRequestToJoinStatus;
import com.fleencorp.feen.shared.chat.space.contract.IsAChatSpace;
import com.fleencorp.feen.shared.chat.space.contract.IsAChatSpaceMember;
import com.fleencorp.feen.shared.chat.space.service.ChatSpaceQueryService;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ChatSpaceQueryServiceImpl implements ChatSpaceQueryService {

  private final EntityManager entityManager;

  public ChatSpaceQueryServiceImpl(EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  @Override
  public Optional<IsAChatSpace> findChatSpaceById(Long chatSpaceId) {
    List<IsAChatSpace> results = entityManager.createQuery(
        "SELECT c.chatSpaceId AS chatSpaceId, c.title AS title FROM ChatSpace c WHERE c.chatSpaceId = :id",
        IsAChatSpace.class)
      .setParameter("id", chatSpaceId)
      .getResultList();

    return results.stream().findFirst();
  }

  @Override
  @SuppressWarnings("unchecked")
  public Optional<IsAChatSpaceMember> findByChatSpaceAndMemberAndStatus(
    Long chatSpaceId,
    Long memberId,
    ChatSpaceRequestToJoinStatus requestToJoinStatus
  ) {
    List<IsAChatSpaceMember> results = entityManager.createNativeQuery(
        """
        SELECT 
            csm.chat_space_member_id AS chatSpaceMemberId,
            cs.parent_external_id_or_name AS parentExternalIdOrName,
            cs.external_id_or_name AS externalIdOrName,
            cs.chat_space_id AS chatSpaceId,
            m.member_id AS memberId,
            csm.left AS hasLeft,
            csm.removed AS removed,
            csm.member_comment AS memberComment,
            csm.space_admin_comment AS spaceAdminComment,
            m.email_address AS emailAddress,
            CONCAT(m.first_name, ' ', m.last_name) AS fullName,
            m.username AS username,
            m.profile_photo_url AS profilePhoto
        FROM chat_space_member csm
        JOIN chat_space cs ON cs.chat_space_id = csm.chat_space_id
        JOIN member m ON m.member_id = csm.member_id
        WHERE csm.chat_space_id = :chatSpaceId
          AND csm.member_id = :memberId
          AND csm.request_to_join_status = :joinStatus
        """,
        IsAChatSpaceMember.class
      )
      .setParameter("chatSpaceId", chatSpaceId)
      .setParameter("memberId", memberId)
      .setParameter("joinStatus", requestToJoinStatus.name())
      .getResultList();

    return results.stream().findFirst();
  }

}
