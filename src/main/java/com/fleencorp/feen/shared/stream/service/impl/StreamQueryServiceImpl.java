package com.fleencorp.feen.shared.stream.service.impl;

import com.fleencorp.feen.shared.stream.contract.IsAStream;
import com.fleencorp.feen.shared.stream.contract.IsAttendee;
import com.fleencorp.feen.shared.stream.model.StreamAttendeeData;
import com.fleencorp.feen.shared.stream.model.StreamData;
import com.fleencorp.feen.shared.stream.query.constant.StreamAttendeeQueryConstant;
import com.fleencorp.feen.shared.stream.query.constant.StreamQueryConstant;
import com.fleencorp.feen.shared.stream.query.mapper.attendee.StreamAttendeeQueryMapper;
import com.fleencorp.feen.shared.stream.query.mapper.stream.StreamQueryMapper1;
import com.fleencorp.feen.shared.stream.query.mapper.stream.StreamQueryMapper2;
import com.fleencorp.feen.shared.stream.query.mapper.stream.StreamQueryMapper4;
import com.fleencorp.feen.shared.stream.query.mapper.stream.StreamQueryMapper5;
import com.fleencorp.feen.shared.stream.service.StreamQueryService;
import com.fleencorp.feen.stream.constant.attendee.StreamAttendeeRequestToJoinStatus;
import com.fleencorp.feen.stream.constant.core.StreamStatus;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static com.fleencorp.feen.shared.stream.query.constant.StreamQueryConstant.FIND_STREAMS_CREATED_BY_MEMBER;

@Service("sharedStreamQueryService")
public class StreamQueryServiceImpl implements StreamQueryService {

  private final EntityManager entityManager;
  private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

  public StreamQueryServiceImpl(
      final EntityManager entityManager,
      final NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
    this.entityManager = entityManager;
    this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
  }

  @Override
  public Optional<IsAStream> findStreamById(Long streamId) {
    final MapSqlParameterSource params = new MapSqlParameterSource()
      .addValue("id", streamId);

    List<StreamData> results = namedParameterJdbcTemplate.query(
      StreamQueryConstant.FIND_STREAM_ID_AND_TITLE_BY_ID,
      params,
      StreamQueryMapper1.of()
    );

    return results.stream()
      .map(IsAStream.class::cast)
      .findFirst();
  }

  @Override
  public Optional<IsAStream> findStreamChatSpaceById(Long streamId) {
    final MapSqlParameterSource params = new MapSqlParameterSource()
      .addValue("id", streamId);

    List<StreamData> results = namedParameterJdbcTemplate.query(
      StreamQueryConstant.FIND_STREAM_CHAT_SPACE_BY_ID,
      params,
      StreamQueryMapper2.of()
    );

    return results.stream()
      .map(IsAStream.class::cast)
      .findFirst();
  }

  @Override
  public Optional<IsAttendee> findAttendeeById(Long attendeeId) {
    MapSqlParameterSource params = new MapSqlParameterSource()
      .addValue("id", attendeeId);

    List<StreamAttendeeData> results = namedParameterJdbcTemplate.query(
      StreamAttendeeQueryConstant.FIND_ATTENDEE_BY_ID,
      params,
      StreamAttendeeQueryMapper.of()
    );

    return results.stream()
      .map(IsAttendee.class::cast)
      .findFirst();
  }

  @Override
  @SuppressWarnings("unchecked")
  public Page<IsAStream> findCommonPastAttendedStreams(
    Long memberAId,
    Long memberBId,
    Collection<StreamAttendeeRequestToJoinStatus> approvedStatuses,
    Collection<StreamStatus> includedStatuses,
    Pageable pageable
  ) {
    MapSqlParameterSource params = new MapSqlParameterSource()
      .addValue("memberAId", memberAId)
      .addValue("memberBId", memberBId)
      .addValue("approvedStatuses", approvedStatuses)
      .addValue("includedStatuses", includedStatuses);

    List<StreamData> results = namedParameterJdbcTemplate.query(
      StreamQueryConstant.FIND_COMMON_PAST_ATTENDED_STREAMS,
      params,
      StreamQueryMapper4.of()
    );

    long total = results.size();

    return new PageImpl<>(
      (List<IsAStream>) (List<?>) results,
      pageable,
      total
    );
  }

  @Override
  @SuppressWarnings("unchecked")
  public Page<IsAStream> findStreamsCreatedByMember(Long memberId, Collection<StreamStatus> includedStatuses, Pageable pageable) {
    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("memberId", memberId);
    params.addValue("includedStatuses", includedStatuses);
    params.addValue("limit", pageable.getPageSize());
    params.addValue("offset", pageable.getOffset());

    List<StreamData> results = namedParameterJdbcTemplate.query(
      FIND_STREAMS_CREATED_BY_MEMBER,
      params,
      StreamQueryMapper5.of()
    );

    long total = results.size();

    return new PageImpl<>(
      (List<IsAStream>) (List<?>) results,
      pageable,
      total
    );
  }

}
