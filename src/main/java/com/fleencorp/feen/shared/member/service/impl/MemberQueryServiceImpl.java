package com.fleencorp.feen.shared.member.service.impl;

import com.fleencorp.feen.shared.member.MemberNotFoundException;
import com.fleencorp.feen.shared.member.contract.IsAMember;
import com.fleencorp.feen.shared.member.model.MemberData;
import com.fleencorp.feen.shared.member.query.constant.MemberQueryConstant;
import com.fleencorp.feen.shared.member.query.mapper.MemberQueryMapper;
import com.fleencorp.feen.shared.member.service.MemberQueryService;
import jakarta.persistence.EntityManager;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MemberQueryServiceImpl implements MemberQueryService {

  private final EntityManager entityManager;
  private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

  public MemberQueryServiceImpl(
      final EntityManager entityManager,
      final NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
    this.entityManager = entityManager;
    this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
  }

  @Override
  public Optional<IsAMember> findMemberById(Long memberId) {
    final MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("id", memberId);

    List<MemberData> results = namedParameterJdbcTemplate.query(
      MemberQueryConstant.FIND_MEMBER_BY_ID,
      params,
      MemberQueryMapper.of()
    );

    return results.stream()
      .map(IsAMember.class::cast)
      .findFirst();
  }


  @Override
  public Optional<IsAMember> findByEmailAddress(String emailAddress) {
    List<IsAMember> results = entityManager.createQuery(
        "SELECT m.memberId, m.emailAddress FROM Member m WHERE m.emailAddress = :email",
        IsAMember.class)
      .setParameter("email", emailAddress)
      .getResultList();

    return results.stream().findFirst();
  }


  @Override
  public IsAMember findMemberOrThrow(Long memberId) {
    return findMemberById(memberId)
      .orElseThrow(MemberNotFoundException.of(memberId));
  }
}
