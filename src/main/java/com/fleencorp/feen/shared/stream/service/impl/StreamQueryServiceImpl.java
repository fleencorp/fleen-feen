package com.fleencorp.feen.shared.stream.service.impl;

import com.fleencorp.feen.shared.stream.contract.IsAStream;
import com.fleencorp.feen.shared.stream.service.StreamQueryService;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Qualifier("sharedStreamQueryService")
public class StreamQueryServiceImpl implements StreamQueryService {

  private final EntityManager entityManager;

  public StreamQueryServiceImpl(final EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  @Override
  public Optional<IsAStream> findStreamById(Long streamId) {
    List<IsAStream> results = entityManager.createQuery(
        "SELECT s.streamId AS streamId, s.title AS title FROM FleenStream s WHERE s.streamId = :id",
        IsAStream.class)
      .setParameter("id", streamId)
      .getResultList();

    return results.stream().findFirst();
  }
}
