package com.fleencorp.feen.service.chat.space.member;


import com.fleencorp.feen.constant.chat.space.ChatSpaceRequestToJoinStatus;
import com.fleencorp.feen.constant.chat.space.member.ChatSpaceMemberRole;
import com.fleencorp.feen.model.domain.chat.ChatSpace;
import com.fleencorp.feen.model.domain.chat.ChatSpaceMember;
import com.fleencorp.feen.model.projection.chat.space.ChatSpaceMemberSelect;
import com.fleencorp.feen.model.projection.chat.space.ChatSpaceRequestToJoinPendingSelect;
import com.fleencorp.feen.user.model.domain.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ChatSpaceMemberOperationsService {

  Page<ChatSpaceMember> findByChatSpaceAndMemberName(ChatSpace chatSpace, String memberName, Pageable pageable);

  Page<ChatSpaceMember> findByChatSpace(ChatSpace chatSpace, Pageable pageable);

  Page<ChatSpaceMember> findAdminByChatSpaceAndMemberName(ChatSpace chatSpace, String memberName, Pageable pageable);

  Page<ChatSpaceMember> findAdminByChatSpace(ChatSpace chatSpace, Pageable pageable);

  Page<ChatSpaceMember> findByChatSpaceAndRequestToJoinStatus(ChatSpace chatSpace, Set<ChatSpaceRequestToJoinStatus> requestToJoinStatus, Pageable pageable);

  Page<ChatSpaceMember> findByChatSpaceAndMemberNameAndRequestToJoinStatus(ChatSpace chatSpace, String memberName, Set<ChatSpaceRequestToJoinStatus> requestToJoinStatus, Pageable pageable);

  Page<ChatSpaceMember> findActiveChatSpaceMembers(ChatSpace chatSpace, ChatSpaceRequestToJoinStatus requestToJoinStatus, Pageable pageable);

  Page<ChatSpaceMember> findByChatSpaceAndMemberNameAndRemoved(ChatSpace chatSpace, String memberName, Pageable pageable);

  Page<ChatSpaceMember> findByChatSpaceAndRemoved(ChatSpace chatSpace, Pageable pageable);

  List<ChatSpaceMemberSelect> findByMemberAndChatSpaceIds(Member member, List<Long> chatSpaceIds);

  List<ChatSpaceRequestToJoinPendingSelect> countPendingJoinRequestsForChatSpaces(List<Long> chatSpaceIds, ChatSpaceRequestToJoinStatus status);

  Optional<ChatSpaceMember> findByChatSpaceAndMember(ChatSpace chatSpace, Member member);

  Optional<ChatSpaceMember> findByChatSpaceAndMember(ChatSpace chatSpace, Long chatSpaceMemberId);

  Optional<ChatSpaceMember> findByChatSpaceAndMemberAndStatus(ChatSpace chatSpace, Member member, ChatSpaceRequestToJoinStatus requestToJoinStatus);

  Optional<ChatSpaceMember> findByChatSpaceMemberAndChatSpace(ChatSpaceMember chatSpaceMember, ChatSpace chatSpace);

  Set<ChatSpaceMember> findByChatSpaceAndRole(ChatSpace chatSpace, ChatSpaceMemberRole role);

  Page<ChatSpaceMember> findSpaceIBelongByDateBetween(LocalDateTime startDate, LocalDateTime endDate, Member member, Pageable pageable);

  Page<ChatSpaceMember> findSpaceIBelongByTitle(String title, Member member, Pageable pageable);

  Page<ChatSpaceMember> findSpaceIBelongMany(Member member, Pageable pageable);

  ChatSpaceMember save(ChatSpaceMember chatSpaceMember);

  Boolean existsByMembers(Long memberId, Long targetMemberId);
}
