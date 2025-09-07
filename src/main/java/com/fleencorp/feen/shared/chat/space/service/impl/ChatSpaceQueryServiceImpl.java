package com.fleencorp.feen.shared.chat.space.service.impl;

import com.fleencorp.feen.chat.space.constant.core.ChatSpaceRequestToJoinStatus;
import com.fleencorp.feen.shared.chat.space.contract.IsAChatSpace;
import com.fleencorp.feen.shared.chat.space.contract.IsAChatSpaceMember;
import com.fleencorp.feen.shared.chat.space.model.ChatSpaceData;
import com.fleencorp.feen.shared.chat.space.model.ChatSpaceMemberData;
import com.fleencorp.feen.shared.chat.space.query.constant.ChatSpaceMemberQueryConstant;
import com.fleencorp.feen.shared.chat.space.query.constant.ChatSpaceQueryConstant;
import com.fleencorp.feen.shared.chat.space.query.mapper.ChatSpaceMemberQueryMapper;
import com.fleencorp.feen.shared.chat.space.query.mapper.ChatSpaceQueryMapper;
import com.fleencorp.feen.shared.chat.space.service.ChatSpaceQueryService;
import jakarta.persistence.EntityManager;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ChatSpaceQueryServiceImpl implements ChatSpaceQueryService {

  private final EntityManager entityManager;
  private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

  public ChatSpaceQueryServiceImpl(
      final EntityManager entityManager,
      final NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
    this.entityManager = entityManager;
    this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
  }

  @Override
  public Optional<IsAChatSpace> findChatSpaceById(Long chatSpaceId) {
    final MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("id", chatSpaceId);

    List<ChatSpaceData> results = namedParameterJdbcTemplate.query(
      ChatSpaceQueryConstant.FIND_CHAT_SPACE_BY_ID,
      params,
      ChatSpaceQueryMapper.of()
    );

    return results.stream()
      .map(IsAChatSpace.class::cast)
      .findFirst();
  }

  @Override
  public Optional<IsAChatSpaceMember> findByChatSpaceAndMemberAndStatus(
    Long chatSpaceId,
    Long memberId,
    ChatSpaceRequestToJoinStatus requestToJoinStatus
  ) {
    final MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("chatSpaceId", chatSpaceId);
    params.addValue("memberId", memberId);
    params.addValue("joinStatus", requestToJoinStatus.name());

    List<ChatSpaceMemberData> results = namedParameterJdbcTemplate.query(
      ChatSpaceMemberQueryConstant.FIND_BY_CHAT_SPACE_AND_MEMBER_AND_STATUS,
      params,
      ChatSpaceMemberQueryMapper.of()
    );

    return results.stream()
      .map(IsAChatSpaceMember.class::cast)
      .findFirst();
  }

}
