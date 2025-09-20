package com.fleencorp.feen.shared.shared.count.repository.impl;

import com.fleencorp.feen.shared.shared.count.repository.ShareCountRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

@Repository
public class ShareCountRepositoryImpl implements ShareCountRepository {

  @PersistenceContext
  private EntityManager entityManager;

  @Override
  @Transactional
  public void incrementSoftAskShareCount(Long id) {
    entityManager.createNativeQuery(
        "UPDATE soft_ask SET share_count = share_count + 1 WHERE soft_ask_id = :id")
      .setParameter("id", id)
      .executeUpdate();
  }

  @Override
  @Transactional
  public void incrementSoftAskReplyShareCount(Long id) {
    entityManager.createNativeQuery(
        "UPDATE soft_ask_reply SET share_count = share_count + 1 WHERE soft_ask_reply_id = :id")
      .setParameter("id", id)
      .executeUpdate();
  }

  @Transactional
  @Override
  public void incrementPollShareCount(Long id) {
    entityManager.createNativeQuery(
        "UPDATE poll SET share_count = share_count + 1 WHERE poll_id = :id")
      .setParameter("id", id)
      .executeUpdate();
  }

  @Override
  @Transactional
  public void incrementStreamShareCount(Long id) {
    entityManager.createNativeQuery(
        "UPDATE stream SET share_count = share_count + 1 WHERE stream_id = :id")
      .setParameter("id", id)
      .executeUpdate();
  }

  @Override
  @Transactional
  public void incrementChatSpaceShareCount(Long id) {
    entityManager.createNativeQuery(
        "UPDATE chat_space SET share_count = share_count + 1 WHERE chat_space_id = :id")
      .setParameter("id", id)
      .executeUpdate();
  }

  @Override
  @Transactional
  public void incrementBusinessShareCount(Long id) {
    entityManager.createNativeQuery(
        "UPDATE business SET share_count = share_count + 1 WHERE business_id = :id")
      .setParameter("id", id)
      .executeUpdate();
  }
}

