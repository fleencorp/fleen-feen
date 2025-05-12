package com.fleencorp.feen.service.impl.chat.space;

import com.fleencorp.feen.constant.chat.space.ChatSpaceRequestToJoinStatus;
import com.fleencorp.feen.constant.chat.space.ChatSpaceStatus;
import com.fleencorp.feen.model.domain.chat.ChatSpace;
import com.fleencorp.feen.model.domain.user.Member;
import com.fleencorp.feen.repository.chat.space.ChatSpaceParticipationRepository;
import com.fleencorp.feen.repository.chat.space.ChatSpaceRepository;
import com.fleencorp.feen.repository.chat.space.UserChatSpaceRepository;
import com.fleencorp.feen.service.chat.space.ChatSpaceOperationsService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collection;

@Service
public class ChatSpaceOperationsServiceImpl implements ChatSpaceOperationsService {

  private final ChatSpaceParticipationRepository chatSpaceParticipationRepository;
  private final ChatSpaceRepository chatSpaceRepository;
  private final UserChatSpaceRepository userChatSpaceRepository;

  public ChatSpaceOperationsServiceImpl(
    final ChatSpaceParticipationRepository chatSpaceParticipationRepository,
    final ChatSpaceRepository chatSpaceRepository,
    final UserChatSpaceRepository userChatSpaceRepository) {
    this.chatSpaceParticipationRepository = chatSpaceParticipationRepository;
    this.chatSpaceRepository = chatSpaceRepository;
    this.userChatSpaceRepository = userChatSpaceRepository;
  }

  @Override
  public Page<ChatSpace> findCommonChatSpaces(
    final Long memberAId,
    final Long memberBId,
    final Collection<ChatSpaceRequestToJoinStatus> approvedStatuses,
    final Collection<ChatSpaceStatus> activeStatuses,
    final Pageable pageable) {
    return chatSpaceParticipationRepository.findCommonChatSpaces(memberAId, memberBId, approvedStatuses, activeStatuses, pageable);
  }

  @Override
  public Page<ChatSpace> findByDateBetween(final LocalDateTime startDate, final LocalDateTime endDate, final ChatSpaceStatus status, final Pageable pageable) {
    return chatSpaceRepository.findByDateBetween(startDate, endDate, status, pageable);
  }

  @Override
  public Page<ChatSpace> findByTitle(
    final String title,
    final ChatSpaceStatus status,
    final Pageable pageable) {
    return chatSpaceRepository.findByTitle(title, status, pageable);
  }

  @Override
  public Page<ChatSpace> findMany(final ChatSpaceStatus status, final Pageable pageable) {
    return chatSpaceRepository.findMany(status, pageable);
  }

  @Override
  @Transactional
  public void incrementTotalMembers(final Long chatSpaceId) {
    chatSpaceRepository.incrementTotalMembers(chatSpaceId);
  }

  @Override
  @Transactional
  public void decrementTotalMembers(final Long chatSpaceId) {
    chatSpaceRepository.decrementTotalMembers(chatSpaceId);
  }

  @Override
  @Transactional
  public int incrementAndGetLikeCount(final Long chatSpaceId) {
    return chatSpaceRepository.incrementAndGetLikeCount(chatSpaceId);
  }

  @Override
  @Transactional
  public int decrementAndGetLikeCount(final Long chatSpaceId) {
    return chatSpaceRepository.decrementAndGetLikeCount(chatSpaceId);
  }

  @Override
  public Page<ChatSpace> findByDateBetweenForUser(final LocalDateTime startDate, final LocalDateTime endDate, final Member member, final Pageable pageable) {
    return userChatSpaceRepository.findByDateBetween(startDate, endDate, member, pageable);
  }

  @Override
  public Page<ChatSpace> findByTitleForUser(final String title, final Member member, final Pageable pageable) {
    return userChatSpaceRepository.findByTitle(title, member, pageable);
  }

  @Override
  public Page<ChatSpace> findManyForUser(final Member member, final Pageable pageable) {
    return userChatSpaceRepository.findMany(member, pageable);
  }
}
