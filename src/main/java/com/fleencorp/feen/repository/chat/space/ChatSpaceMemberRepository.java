package com.fleencorp.feen.repository.chat.space;

import com.fleencorp.feen.constant.chat.space.ChatSpaceRequestToJoinStatus;
import com.fleencorp.feen.constant.chat.space.member.ChatSpaceMemberRole;
import com.fleencorp.feen.model.domain.chat.ChatSpace;
import com.fleencorp.feen.model.domain.chat.ChatSpaceMember;
import com.fleencorp.feen.model.domain.user.Member;
import com.fleencorp.feen.model.projection.chat.space.ChatSpaceMemberSelect;
import com.fleencorp.feen.model.projection.chat.space.ChatSpaceRequestToJoinPendingSelect;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ChatSpaceMemberRepository extends JpaRepository<ChatSpaceMember, Long> {

  @Query(value =
    """
        SELECT csm FROM ChatSpaceMember csm
        WHERE csm.chatSpace = :chatSpace AND (csm.member.firstName = :name OR csm.member.lastName = :name)
        AND csm.removed = false
        ORDER BY
        CASE WHEN csm.role = 'ADMIN' THEN 0 ELSE 1 END,
        csm.updatedOn DESC
    """)
  Page<ChatSpaceMember> findByChatSpaceAndMemberName(@Param("chatSpace") ChatSpace chatSpace, @Param("name") String memberName, Pageable pageable);

  @Query(value =
    """
        SELECT csm FROM ChatSpaceMember csm
        WHERE csm.chatSpaceMemberId IS NOT NULL AND csm.chatSpace = :chatSpace
        AND csm.removed = false
        ORDER BY
        CASE WHEN csm.role = 'ADMIN' THEN 0 ELSE 1 END,
        csm.updatedOn DESC
    """)
  Page<ChatSpaceMember> findByChatSpace(@Param("chatSpace") ChatSpace chatSpace, Pageable pageable);

  @Query(value = "SELECT csm FROM ChatSpaceMember csm WHERE csm.chatSpace = :chatSpace AND csm.role = 'ADMIN' AND (csm.member.firstName = :name OR csm.member.lastName = :name)")
  Page<ChatSpaceMember> findAdminByChatSpaceAndMemberName(@Param("chatSpace") ChatSpace chatSpace, @Param("name") String memberName, Pageable pageable);

  @Query("SELECT csm FROM ChatSpaceMember csm WHERE csm.chatSpaceMemberId IS NOT NULL AND csm.chatSpace = :chatSpace AND csm.role = 'ADMIN' ORDER BY csm.updatedOn DESC")
  Page<ChatSpaceMember> findAdminByChatSpace(@Param("chatSpace") ChatSpace chatSpace, Pageable pageable);

  Optional<ChatSpaceMember> findByChatSpaceAndMember(ChatSpace chatSpace, Member member);

  @Query("SELECT csm FROM ChatSpaceMember csm WHERE csm.chatSpace = :chatSpace AND csm.chatSpaceMemberId = :chatSpaceMemberId")
  Optional<ChatSpaceMember> findByChatSpaceAndMember(ChatSpace chatSpace, Long chatSpaceMemberId);

  @Query("SELECT csm FROM ChatSpaceMember csm WHERE csm.chatSpace = :chatSpace AND csm.member = :member AND csm.requestToJoinStatus = :joinStatus")
  Optional<ChatSpaceMember> findByChatSpaceAndMemberAndStatus(@Param("chatSpace") ChatSpace chatSpace, @Param("member") Member member, @Param("joinStatus") ChatSpaceRequestToJoinStatus requestToJoinStatus);

  @Query("SELECT csm FROM ChatSpaceMember csm WHERE csm = :chatSpaceMember AND csm.chatSpace = :chatSpace")
  Optional<ChatSpaceMember> findByChatSpaceMemberAndChatSpace(@Param("chatSpaceMember") ChatSpaceMember chatSpaceMember, @Param("chatSpace") ChatSpace chatSpace);

  @Query("SELECT csm FROM ChatSpaceMember csm WHERE csm.chatSpace = :chatSpace AND csm.role = :role")
  Set<ChatSpaceMember> findByChatSpaceAndRole(@Param("chatSpace") ChatSpace chatSpace, @Param("role") ChatSpaceMemberRole role);

  @Query(value = "SELECT csm FROM ChatSpaceMember csm WHERE csm.chatSpace = :chatSpace AND csm.requestToJoinStatus IN (:requestToJoinStatuses) ORDER BY csm.updatedOn DESC")
  Page<ChatSpaceMember> findByChatSpaceAndRequestToJoinStatus(ChatSpace chatSpace, @Param("requestToJoinStatuses") Set<ChatSpaceRequestToJoinStatus> requestToJoinStatus, Pageable pageable);


  @Query(value =
    """
        SELECT csm FROM ChatSpaceMember csm WHERE csm.chatSpace = :chatSpace
        AND (csm.member.firstName = :name OR csm.member.lastName = :name)
        AND csm.requestToJoinStatus IN (:requestToJoinStatuses) ORDER BY csm.updatedOn DESC
    """)
  Page<ChatSpaceMember> findByChatSpaceAndMemberNameAndRequestToJoinStatus(ChatSpace chatSpace, @Param("name") String memberName, @Param("requestToJoinStatuses") Set<ChatSpaceRequestToJoinStatus> requestToJoinStatus, Pageable pageable);


  @EntityGraph(attributePaths = {"chatSpace"})
  @Query(value =
    """
    SELECT csm FROM ChatSpaceMember csm WHERE csm.chatSpace.createdOn BETWEEN :startDate AND :endDate AND csm.member = :member AND csm.chatSpace.member != :member
    ORDER BY csm.updatedOn DESC
    """)
  Page<ChatSpaceMember> findSpaceIBelongByDateBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, @Param("member") Member member, Pageable pageable);


  @EntityGraph(attributePaths = {"chatSpace"})
  @Query(value =
    """
        SELECT csm FROM ChatSpaceMember csm WHERE csm.chatSpace.title = :title AND csm.member = :member AND csm.chatSpace.member != :member ORDER BY csm.updatedOn DESC
    """)
  Page<ChatSpaceMember> findSpaceIBelongByTitle(@Param("title") String title, @Param("member") Member member, Pageable pageable);


  @EntityGraph(attributePaths = {"chatSpace"})
  @Query(value =
    """
        SELECT csm FROM ChatSpaceMember csm WHERE csm.member = :member AND csm.chatSpace.member != :member ORDER BY csm.updatedOn DESC
    """)
  Page<ChatSpaceMember> findSpaceIBelongMany(@Param("member") Member member, Pageable pageable);


  @Query(value =
    """
        SELECT new com.fleencorp.feen.model.projection.chat.space.ChatSpaceMemberSelect(cs.chatSpaceId, csm.requestToJoinStatus, csm.chatSpace.spaceVisibility, csm.left, csm.removed, csm.role)
        FROM ChatSpaceMember csm LEFT JOIN csm.member m LEFT JOIN csm.chatSpace cs WHERE m = :member AND cs.chatSpaceId IN (:ids)
    """)
  List<ChatSpaceMemberSelect> findByMemberAndChatSpaceIds(Member member, @Param("ids") List<Long> chatSpaceIds);


  @Query(value =
    """
        SELECT new com.fleencorp.feen.model.projection.chat.space.ChatSpaceRequestToJoinPendingSelect(csm.chatSpace.chatSpaceId, COUNT(csm))
        FROM ChatSpaceMember csm WHERE csm.chatSpace.chatSpaceId IN :chatSpaceIds
        AND csm.requestToJoinStatus = :status GROUP BY csm.chatSpace.chatSpaceId
    """)
  List<ChatSpaceRequestToJoinPendingSelect> countPendingJoinRequestsForChatSpaces(@Param("chatSpaceIds") List<Long> chatSpaceIds, @Param("status") ChatSpaceRequestToJoinStatus status);


  @Query("SELECT csm FROM ChatSpaceMember csm WHERE csm.chatSpace = :chatSpace AND csm.requestToJoinStatus = :requestToJoinStatus AND csm.left = false AND csm.removed = false")
  Page<ChatSpaceMember> findActiveChatSpaceMembers(@Param("chatSpace") ChatSpace chatSpace, @Param("requestToJoinStatus") ChatSpaceRequestToJoinStatus requestToJoinStatus, Pageable pageable);

  @Query(value =
    """
        SELECT csm FROM ChatSpaceMember csm WHERE csm.chatSpace = :chatSpace
        AND (csm.member.firstName = :name OR csm.member.lastName = :name)
        AND csm.removed = true ORDER BY csm.updatedOn DESC
    """)
  Page<ChatSpaceMember> findByChatSpaceAndMemberNameAndRemoved(ChatSpace chatSpace, @Param("name") String memberName, Pageable pageable);

  @Query(value = "SELECT csm FROM ChatSpaceMember csm WHERE csm.chatSpace = :chatSpace AND csm.removed = true ORDER BY csm.updatedOn DESC")
  Page<ChatSpaceMember> findByChatSpaceAndRemoved(ChatSpace chatSpace, Pageable pageable);
}
