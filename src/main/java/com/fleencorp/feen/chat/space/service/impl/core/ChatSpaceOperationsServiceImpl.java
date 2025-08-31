package com.fleencorp.feen.chat.space.service.impl.core;

import com.fleencorp.feen.chat.space.constant.core.ChatSpaceRequestToJoinStatus;
import com.fleencorp.feen.chat.space.constant.core.ChatSpaceStatus;
import com.fleencorp.feen.chat.space.exception.core.ChatSpaceNotAnAdminException;
import com.fleencorp.feen.chat.space.exception.core.ChatSpaceNotFoundException;
import com.fleencorp.feen.chat.space.model.domain.ChatSpace;
import com.fleencorp.feen.chat.space.repository.ChatSpaceParticipationRepository;
import com.fleencorp.feen.chat.space.repository.ChatSpaceRepository;
import com.fleencorp.feen.chat.space.repository.UserChatSpaceRepository;
import com.fleencorp.feen.chat.space.service.core.ChatSpaceOperationsService;
import com.fleencorp.feen.chat.space.service.core.ChatSpaceService;
import com.fleencorp.feen.common.exception.FailedOperationException;
import com.fleencorp.feen.user.model.domain.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Service
public class ChatSpaceOperationsServiceImpl implements ChatSpaceOperationsService {

  private final ChatSpaceService chatSpaceService;
  private final ChatSpaceParticipationRepository chatSpaceParticipationRepository;
  private final ChatSpaceRepository chatSpaceRepository;
  private final UserChatSpaceRepository userChatSpaceRepository;

  public ChatSpaceOperationsServiceImpl(
      final ChatSpaceService chatSpaceService,
      final ChatSpaceParticipationRepository chatSpaceParticipationRepository,
      final ChatSpaceRepository chatSpaceRepository,
      final UserChatSpaceRepository userChatSpaceRepository) {
    this.chatSpaceService = chatSpaceService;
    this.chatSpaceParticipationRepository = chatSpaceParticipationRepository;
    this.chatSpaceRepository = chatSpaceRepository;
    this.userChatSpaceRepository = userChatSpaceRepository;
  }

  @Override
  public ChatSpace findChatSpace(final Long chatSpaceId) {
    return chatSpaceRepository.findById(chatSpaceId)
      .orElseThrow(ChatSpaceNotFoundException.of(chatSpaceId));
  }

  @Override
  public ChatSpace findChatSpaceAndVerifyAdmin(final Long chatSpaceId, final Member member) {
    final ChatSpace chatSpace = findChatSpace(chatSpaceId);
    chatSpaceService.verifyCreatorOrAdminOfChatSpace(chatSpace, member);

    return chatSpace;
  }

  @Override
  public boolean checkIsAdmin(final ChatSpace chatSpace, final Member member) {
    return chatSpaceService.verifyCreatorOrAdminOfChatSpaceNoThrow(chatSpace, member.getMemberId());
  }

  @Override
  public void verifyCreatorOrAdminOfChatSpace(final ChatSpace chatSpace, final Member member) throws FailedOperationException, ChatSpaceNotAnAdminException {
    chatSpaceService.verifyCreatorOrAdminOfChatSpace(chatSpace, member);
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
  public Page<ChatSpace> findCommonChatSpaces(
      final Member memberA,
      final Member memberB,
      final Pageable pageable) {
    return chatSpaceParticipationRepository.findCommonChatSpaces(
      memberA.getMemberId(),
      memberB.getMemberId(),
      List.of(ChatSpaceRequestToJoinStatus.APPROVED),
      List.of(ChatSpaceStatus.ACTIVE),
      pageable);
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
