package com.fleencorp.feen.shared.poll.service.impl;

import com.fleencorp.feen.shared.poll.contract.IsAPoll;
import com.fleencorp.feen.shared.poll.model.PollData;
import com.fleencorp.feen.shared.poll.query.constant.PollQueryConstant;
import com.fleencorp.feen.shared.poll.query.mapper.PollQueryMapper;
import com.fleencorp.feen.shared.poll.service.PollQueryService;
import jakarta.persistence.EntityManager;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PollQueryServiceImpl implements PollQueryService {

  private final EntityManager entityManager;
  private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

  public PollQueryServiceImpl(
      final EntityManager entityManager,
      final NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
    this.entityManager = entityManager;
    this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
  }

  @Override
  public Optional<IsAPoll> findPollById(Long pollId) {
    final MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("id", pollId);

    List<PollData> results = namedParameterJdbcTemplate.query(
      PollQueryConstant.FIND_POLLS,
      params,
      PollQueryMapper.of()
    );

    return results.stream()
      .map(IsAPoll.class::cast)
      .findFirst();
  }
}
