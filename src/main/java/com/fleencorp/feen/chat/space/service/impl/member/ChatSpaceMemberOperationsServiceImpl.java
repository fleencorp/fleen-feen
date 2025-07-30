package com.fleencorp.feen.chat.space.service.impl.member;

import com.fleencorp.feen.chat.space.constant.core.ChatSpaceRequestToJoinStatus;
import com.fleencorp.feen.chat.space.constant.member.ChatSpaceMemberRole;
import com.fleencorp.feen.chat.space.model.domain.ChatSpace;
import com.fleencorp.feen.chat.space.model.domain.ChatSpaceMember;
import com.fleencorp.feen.chat.space.model.projection.ChatSpaceMemberSelect;
import com.fleencorp.feen.chat.space.model.projection.ChatSpaceRequestToJoinPendingSelect;
import com.fleencorp.feen.chat.space.repository.member.*;
import com.fleencorp.feen.chat.space.service.member.ChatSpaceMemberOperationsService;
import com.fleencorp.feen.user.model.domain.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class ChatSpaceMemberOperationsServiceImpl implements ChatSpaceMemberOperationsService {

  private final ChatSpaceMemberLookupRepository chatSpaceMemberLookupRepository;
  private final ChatSpaceMemberQueryRepository chatSpaceMemberQueryRepository;
  private final ChatSpaceMemberParticipationRepository chatSpaceMemberParticipationRepository;
  private final ChatSpaceMemberProjectionRepository chatSpaceMemberProjectionRepository;
  private final ChatSpaceMemberRepository chatSpaceMemberRepository;

  public ChatSpaceMemberOperationsServiceImpl(
      final ChatSpaceMemberLookupRepository chatSpaceMemberLookupRepository,
      final ChatSpaceMemberQueryRepository chatSpaceMemberQueryRepository,
      final ChatSpaceMemberParticipationRepository chatSpaceMemberParticipationRepository,
      final ChatSpaceMemberProjectionRepository chatSpaceMemberProjectionRepository,
      final ChatSpaceMemberRepository chatSpaceMemberRepository) {
    this.chatSpaceMemberQueryRepository = chatSpaceMemberQueryRepository;
    this.chatSpaceMemberLookupRepository = chatSpaceMemberLookupRepository;
    this.chatSpaceMemberParticipationRepository = chatSpaceMemberParticipationRepository;
    this.chatSpaceMemberProjectionRepository = chatSpaceMemberProjectionRepository;
    this.chatSpaceMemberRepository = chatSpaceMemberRepository;
  }

  @Override
  public Page<ChatSpaceMember> findByChatSpaceAndMemberName(final ChatSpace chatSpace, final String memberName, final Pageable pageable) {
    return chatSpaceMemberQueryRepository.findByChatSpaceAndMemberName(chatSpace, memberName, pageable);
  }

  @Override
  public Page<ChatSpaceMember> findByChatSpace(final ChatSpace chatSpace, final Pageable pageable) {
    return chatSpaceMemberQueryRepository.findByChatSpace(chatSpace, pageable);
  }

  @Override
  public Page<ChatSpaceMember> findAdminByChatSpaceAndMemberName(final ChatSpace chatSpace, final String memberName, final Pageable pageable) {
    return chatSpaceMemberQueryRepository.findAdminByChatSpaceAndMemberName(chatSpace, memberName, pageable);
  }

  @Override
  public Page<ChatSpaceMember> findAdminByChatSpace(final ChatSpace chatSpace, final Pageable pageable) {
    return chatSpaceMemberQueryRepository.findAdminByChatSpace(chatSpace, pageable);
  }

  @Override
  public Page<ChatSpaceMember> findByChatSpaceAndRequestToJoinStatus(final ChatSpace chatSpace, final Set<ChatSpaceRequestToJoinStatus> requestToJoinStatus, final Pageable pageable) {
    return chatSpaceMemberQueryRepository.findByChatSpaceAndRequestToJoinStatus(chatSpace, requestToJoinStatus, pageable);
  }

  @Override
  public Page<ChatSpaceMember> findByChatSpaceAndMemberNameAndRequestToJoinStatus(final ChatSpace chatSpace, final String memberName, final Set<ChatSpaceRequestToJoinStatus> requestToJoinStatus, final Pageable pageable) {
    return chatSpaceMemberQueryRepository.findByChatSpaceAndMemberNameAndRequestToJoinStatus(chatSpace, memberName, requestToJoinStatus, pageable);
  }

  @Override
  public Page<ChatSpaceMember> findActiveChatSpaceMembers(final ChatSpace chatSpace, final ChatSpaceRequestToJoinStatus requestToJoinStatus, final Pageable pageable) {
    return chatSpaceMemberQueryRepository.findActiveChatSpaceMembers(chatSpace, requestToJoinStatus, pageable);
  }

  @Override
  public Page<ChatSpaceMember> findByChatSpaceAndMemberNameAndRemoved(final ChatSpace chatSpace, final String memberName, final Pageable pageable) {
    return chatSpaceMemberQueryRepository.findByChatSpaceAndMemberNameAndRemoved(chatSpace, memberName, pageable);
  }

  @Override
  public Page<ChatSpaceMember> findByChatSpaceAndRemoved(final ChatSpace chatSpace, final Pageable pageable) {
    return chatSpaceMemberQueryRepository.findByChatSpaceAndRemoved(chatSpace, pageable);
  }

  // ChatSpaceMemberProjectionRepository Methods
  @Override
  public List<ChatSpaceMemberSelect> findByMemberAndChatSpaceIds(final Member member, final List<Long> chatSpaceIds) {
    return chatSpaceMemberProjectionRepository.findByMemberAndChatSpaceIds(member, chatSpaceIds);
  }

  @Override
  public List<ChatSpaceRequestToJoinPendingSelect> countPendingJoinRequestsForChatSpaces(final List<Long> chatSpaceIds, final ChatSpaceRequestToJoinStatus status) {
    return chatSpaceMemberProjectionRepository.countPendingJoinRequestsForChatSpaces(chatSpaceIds, status);
  }

  @Override
  public Optional<ChatSpaceMember> findByChatSpaceAndMember(final ChatSpace chatSpace, final Member member) {
    return chatSpaceMemberLookupRepository.findByChatSpaceAndMember(chatSpace, member);
  }

  @Override
  public Optional<ChatSpaceMember> findByChatSpaceAndMember(final ChatSpace chatSpace, final Long chatSpaceMemberId) {
    return chatSpaceMemberLookupRepository.findByChatSpaceAndMember(chatSpace, chatSpaceMemberId);
  }

  @Override
  public Optional<ChatSpaceMember> findByChatSpaceAndMemberAndStatus(final ChatSpace chatSpace, final Member member, final ChatSpaceRequestToJoinStatus requestToJoinStatus) {
    return chatSpaceMemberLookupRepository.findByChatSpaceAndMemberAndStatus(chatSpace, member, requestToJoinStatus);
  }

  @Override
  public Optional<ChatSpaceMember> findByChatSpaceMemberAndChatSpace(final ChatSpaceMember chatSpaceMember, final ChatSpace chatSpace) {
    return chatSpaceMemberLookupRepository.findByChatSpaceMemberAndChatSpace(chatSpaceMember, chatSpace);
  }

  @Override
  public Set<ChatSpaceMember> findByChatSpaceAndRole(final ChatSpace chatSpace, final ChatSpaceMemberRole role) {
    return chatSpaceMemberLookupRepository.findByChatSpaceAndRole(chatSpace, role);
  }

  @Override
  public Page<ChatSpaceMember> findSpaceIBelongByDateBetween(final LocalDateTime startDate, final LocalDateTime endDate, final Member member, final Pageable pageable) {
    return chatSpaceMemberLookupRepository.findSpaceIBelongByDateBetween(startDate, endDate, member, pageable);
  }

  @Override
  public Page<ChatSpaceMember> findSpaceIBelongByTitle(final String title, final Member member, final Pageable pageable) {
    return chatSpaceMemberLookupRepository.findSpaceIBelongByTitle(title, member, pageable);
  }

  @Override
  public Page<ChatSpaceMember> findSpaceIBelongMany(final Member member, final Pageable pageable) {
    return chatSpaceMemberLookupRepository.findSpaceIBelongMany(member, pageable);
  }

  @Override
  public ChatSpaceMember save(final ChatSpaceMember chatSpaceMember) {
    return chatSpaceMemberRepository.save(chatSpaceMember);
  }

  @Override
  public Boolean existsByMembers(final Long memberId, final Long targetMemberId) {
    return Boolean.TRUE.equals(chatSpaceMemberParticipationRepository.existsByMembers(memberId, targetMemberId));
  }
}