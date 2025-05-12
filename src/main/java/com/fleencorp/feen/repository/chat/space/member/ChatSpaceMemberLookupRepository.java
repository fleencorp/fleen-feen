package com.fleencorp.feen.repository.chat.space.member;

import com.fleencorp.feen.constant.chat.space.ChatSpaceRequestToJoinStatus;
import com.fleencorp.feen.constant.chat.space.member.ChatSpaceMemberRole;
import com.fleencorp.feen.model.domain.chat.ChatSpace;
import com.fleencorp.feen.model.domain.chat.ChatSpaceMember;
import com.fleencorp.feen.model.domain.user.Member;
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

  Optional<ChatSpaceMember> findByChatSpaceAndMember(ChatSpace chatSpace, Member member);

  @Query("SELECT csm FROM ChatSpaceMember csm WHERE csm.chatSpace = :chatSpace AND csm.chatSpaceMemberId = :chatSpaceMemberId")
  Optional<ChatSpaceMember> findByChatSpaceAndMember(ChatSpace chatSpace, Long chatSpaceMemberId);

  @Query("SELECT csm FROM ChatSpaceMember csm WHERE csm.chatSpace = :chatSpace AND csm.member = :member AND csm.requestToJoinStatus = :joinStatus")
  Optional<ChatSpaceMember> findByChatSpaceAndMemberAndStatus(
    @Param("chatSpace") ChatSpace chatSpace,
    @Param("member") Member member,
    @Param("joinStatus") ChatSpaceRequestToJoinStatus requestToJoinStatus);

  @Query("SELECT csm FROM ChatSpaceMember csm WHERE csm = :chatSpaceMember AND csm.chatSpace = :chatSpace")
  Optional<ChatSpaceMember> findByChatSpaceMemberAndChatSpace(@Param("chatSpaceMember") ChatSpaceMember chatSpaceMember, @Param("chatSpace") ChatSpace chatSpace);

  @Query("SELECT csm FROM ChatSpaceMember csm WHERE csm.chatSpace = :chatSpace AND csm.role = :role")
  Set<ChatSpaceMember> findByChatSpaceAndRole(@Param("chatSpace") ChatSpace chatSpace, @Param("role") ChatSpaceMemberRole role);

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
