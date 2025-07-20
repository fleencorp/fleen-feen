package com.fleencorp.feen.chat.space.repository.member;

import com.fleencorp.feen.chat.space.constant.core.ChatSpaceRequestToJoinStatus;
import com.fleencorp.feen.chat.space.model.domain.ChatSpace;
import com.fleencorp.feen.chat.space.model.domain.ChatSpaceMember;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface ChatSpaceMemberQueryRepository extends JpaRepository<ChatSpaceMember, Long> {

  @Query(value =
  """
    SELECT csm FROM ChatSpaceMember csm
    WHERE csm.chatSpace = :chatSpace AND (csm.member.firstName = :name OR csm.member.lastName = :name)
    AND csm.removed = false
    ORDER BY
    CASE WHEN csm.role = 'ADMIN' THEN 0 ELSE 1 END,
    csm.updatedOn DESC
  """)
  Page<ChatSpaceMember> findByChatSpaceAndMemberName(
    @Param("chatSpace") ChatSpace chatSpace,
    @Param("name") String memberName,
    Pageable pageable);

  @Query(value =
  """
    SELECT csm FROM ChatSpaceMember csm
    WHERE csm.chatSpaceMemberId IS NOT NULL AND csm.chatSpace = :chatSpace
    AND csm.removed = false
    ORDER BY
    CASE WHEN csm.role = 'ADMIN' THEN 0 ELSE 1 END,
    csm.updatedOn DESC
  """)
  Page<ChatSpaceMember> findByChatSpace(
    @Param("chatSpace") ChatSpace chatSpace,
    Pageable pageable);

  @Query(value =
  """
    SELECT csm FROM ChatSpaceMember csm
    WHERE csm.chatSpace = :chatSpace
    AND csm.role = 'ADMIN'
    AND (csm.member.firstName = :name OR csm.member.lastName = :name)
  """)
  Page<ChatSpaceMember> findAdminByChatSpaceAndMemberName(@Param("chatSpace") ChatSpace chatSpace, @Param("name") String memberName, Pageable pageable);

  @Query(value =
  """
    SELECT csm FROM ChatSpaceMember csm
    WHERE csm.chatSpaceMemberId IS NOT NULL
    AND csm.chatSpace = :chatSpace
    AND csm.role = 'ADMIN'
    ORDER BY csm.updatedOn DESC
  """)
  Page<ChatSpaceMember> findAdminByChatSpace(@Param("chatSpace") ChatSpace chatSpace, Pageable pageable);

  @Query(value =
  """
    SELECT csm FROM ChatSpaceMember csm
    WHERE csm.chatSpace = :chatSpace
    AND csm.requestToJoinStatus IN (:requestToJoinStatuses)
    ORDER BY csm.updatedOn DESC
  """)
  Page<ChatSpaceMember> findByChatSpaceAndRequestToJoinStatus(
    @Param("chatSpace") ChatSpace chatSpace,
    @Param("requestToJoinStatuses") Set<ChatSpaceRequestToJoinStatus> requestToJoinStatus,
    Pageable pageable);

  @Query(value =
  """
    SELECT csm FROM ChatSpaceMember csm
    WHERE csm.chatSpace = :chatSpace
    AND (csm.member.firstName = :name OR csm.member.lastName = :name)
    AND csm.requestToJoinStatus IN (:requestToJoinStatuses)
    ORDER BY csm.updatedOn DESC
  """)
  Page<ChatSpaceMember> findByChatSpaceAndMemberNameAndRequestToJoinStatus(
    @Param("chatSpace") ChatSpace chatSpace,
    @Param("name") String memberName,
    @Param("requestToJoinStatuses") Set<ChatSpaceRequestToJoinStatus> requestToJoinStatus,
    Pageable pageable);

  @Query(value =
  """
    SELECT csm FROM ChatSpaceMember csm
    WHERE csm.chatSpace = :chatSpace
    AND csm.requestToJoinStatus = :requestToJoinStatus
    AND csm.left = false AND csm.removed = false
  """)
  Page<ChatSpaceMember> findActiveChatSpaceMembers(
    @Param("chatSpace") ChatSpace chatSpace,
    @Param("requestToJoinStatus") ChatSpaceRequestToJoinStatus requestToJoinStatus,
    Pageable pageable);

  @Query(value =
  """
    SELECT csm FROM ChatSpaceMember csm
    WHERE csm.chatSpace = :chatSpace
    AND (csm.member.firstName = :name OR csm.member.lastName = :name)
    AND csm.removed = true
    ORDER BY csm.updatedOn DESC
  """)
  Page<ChatSpaceMember> findByChatSpaceAndMemberNameAndRemoved(
    @Param("chatSpace") ChatSpace chatSpace,
    @Param("name") String memberName,
    Pageable pageable);

  @Query(value =
  """
    SELECT csm FROM ChatSpaceMember csm
    WHERE csm.chatSpace = :chatSpace
    AND csm.removed = true
    ORDER BY csm.updatedOn DESC
  """)
  Page<ChatSpaceMember> findByChatSpaceAndRemoved(
    @Param("chatSpace") ChatSpace chatSpace, Pageable pageable);
}
