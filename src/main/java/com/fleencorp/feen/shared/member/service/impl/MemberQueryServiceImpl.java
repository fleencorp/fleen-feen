package com.fleencorp.feen.shared.member.service.impl;

import com.fleencorp.feen.shared.member.MemberNotFoundException;
import com.fleencorp.feen.shared.member.contract.IsAMember;
import com.fleencorp.feen.shared.member.service.MemberQueryService;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MemberQueryServiceImpl implements MemberQueryService {

  private final EntityManager entityManager;

  public MemberQueryServiceImpl(final EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  @Override
  public Optional<IsAMember> findMemberById(Long memberId) {
    List<IsAMember> results = entityManager.createQuery(
        "SELECT m.memberId AS memberId FROM Member m WHERE m.memberId = :id",
        IsAMember.class)
      .setParameter("id", memberId)
      .getResultList();

    return results.stream().findFirst();
  }

  @Override
  public IsAMember findMemberOrThrow(Long memberId) {
    return findMemberById(memberId)
      .orElseThrow(MemberNotFoundException.of(memberId));
  }
}
