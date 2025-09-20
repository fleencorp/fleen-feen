package com.fleencorp.feen.chat.space.repository.member;

import com.fleencorp.feen.chat.space.constant.core.ChatSpaceRequestToJoinStatus;
import com.fleencorp.feen.chat.space.constant.member.ChatSpaceMemberRole;
import com.fleencorp.feen.chat.space.model.domain.ChatSpace;
import com.fleencorp.feen.chat.space.model.domain.ChatSpaceMember;
import com.fleencorp.feen.user.model.domain.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

public interface ChatSpaceMemberLookupRepository extends JpaRepository<ChatSpaceMember, Long> {

  Optional<ChatSpaceMember> findByChatSpaceAndMemberId(ChatSpace chatSpace, Long memberId);

  @Query("SELECT csm FROM ChatSpaceMember csm WHERE csm.chatSpace = :chatSpace AND csm.chatSpaceMemberId = :chatSpaceMemberId")
  Optional<ChatSpaceMember> findByChatSpaceAndMember(ChatSpace chatSpace, Long chatSpaceMemberId);

  @Query("SELECT csm FROM ChatSpaceMember csm WHERE csm.chatSpace = :chatSpace AND csm.member = :member AND csm.requestToJoinStatus = :joinStatus")
  Optional<ChatSpaceMember> findByChatSpaceAndMemberAndStatus(
    @Param("chatSpace") ChatSpace chatSpace,
    @Param("member") Member member,
    @Param("joinStatus") ChatSpaceRequestToJoinStatus requestToJoinStatus);

  @Query("SELECT csm FROM ChatSpaceMember csm WHERE csm = :chatSpaceMember AND csm.chatSpace = :chatSpace")
  Optional<ChatSpaceMember> findByChatSpaceMemberAndChatSpace(@Param("chatSpaceMember") ChatSpaceMember chatSpaceMember, @Param("chatSpace") ChatSpace chatSpace);

  @Query("SELECT csm FROM ChatSpaceMember csm WHERE csm.chatSpace = :chatSpaceId AND csm.role = :role")
  Set<ChatSpaceMember> findByChatSpaceAndRole(@Param("chatSpaceId") Long chatSpaceId, @Param("role") ChatSpaceMemberRole role);

  @EntityGraph(attributePaths = {"chatSpace"})
  @Query(value =
  """
    SELECT csm FROM ChatSpaceMember csm
    WHERE csm.chatSpace.createdOn
    BETWEEN :startDate AND :endDate
    AND csm.member = :member
    AND csm.chatSpace.member != :member
    ORDER BY csm.updatedOn DESC
  """)
  Page<ChatSpaceMember> findSpaceIBelongByDateBetween(
    @Param("startDate") LocalDateTime startDate,
    @Param("endDate") LocalDateTime endDate,
    @Param("member") Member member,
    Pageable pageable);

  @EntityGraph(attributePaths = {"chatSpace"})
  @Query(value =
  """
    SELECT csm FROM ChatSpaceMember csm
    WHERE csm.chatSpace.title = :title
    AND csm.member = :member
    AND csm.chatSpace.member != :member
    ORDER BY csm.updatedOn DESC
  """)
  Page<ChatSpaceMember> findSpaceIBelongByTitle(
    @Param("title") String title,
    @Param("member") Member member,
    Pageable pageable);

  @EntityGraph(attributePaths = {"chatSpace"})
  @Query(value =
  """
    SELECT csm FROM ChatSpaceMember csm
    WHERE csm.member = :member
    AND csm.chatSpace.member != :member
    ORDER BY csm.updatedOn DESC
  """)
  Page<ChatSpaceMember> findSpaceIBelongMany(
    @Param("member") Member member,
    Pageable pageable);
}
