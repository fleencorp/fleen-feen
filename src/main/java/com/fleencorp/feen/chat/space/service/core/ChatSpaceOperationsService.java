package com.fleencorp.feen.chat.space.service.core;

import com.fleencorp.feen.chat.space.constant.core.ChatSpaceRequestToJoinStatus;
import com.fleencorp.feen.chat.space.constant.core.ChatSpaceStatus;
import com.fleencorp.feen.chat.space.model.domain.ChatSpace;
import com.fleencorp.feen.user.model.domain.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Collection;

public interface ChatSpaceOperationsService {

  ChatSpace findChatSpace(Long chatSpaceId);

  Page<ChatSpace> findCommonChatSpaces(
    Long memberAId,
    Long memberBId,
    Collection<ChatSpaceRequestToJoinStatus> approvedStatuses,
    Collection<ChatSpaceStatus> activeStatuses,
    Pageable pageable);

  Page<ChatSpace> findCommonChatSpaces(
    Member memberA,
    Member memberB,
    Pageable pageable);

  Page<ChatSpace> findByDateBetween(LocalDateTime startDate, LocalDateTime endDate, ChatSpaceStatus status, Pageable pageable);

  Page<ChatSpace> findByTitle(String title, ChatSpaceStatus status, Pageable pageable);

  Page<ChatSpace> findMany(ChatSpaceStatus status, Pageable pageable);

  Page<ChatSpace> findByDateBetweenForUser(LocalDateTime startDate, LocalDateTime endDate, Member member, Pageable pageable);

  Page<ChatSpace> findByTitleForUser(String title, Member member, Pageable pageable);

  Page<ChatSpace> findManyForUser(Member member, Pageable pageable);
}
