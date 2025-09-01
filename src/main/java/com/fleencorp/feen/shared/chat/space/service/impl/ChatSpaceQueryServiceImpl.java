package com.fleencorp.feen.shared.chat.space.service.impl;

import com.fleencorp.feen.shared.chat.space.contract.IsAChatSpace;
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
}
