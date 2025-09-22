package com.fleencorp.feen.softask.repository.softask.impl;

import com.fleencorp.feen.softask.dao.mapper.SoftAskWithDetailMapper;
import com.fleencorp.feen.softask.model.projection.SoftAskWithDetail;
import com.fleencorp.feen.softask.repository.softask.SoftAskSearchCustomRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class SoftAskSearchCustomRepositoryImpl implements SoftAskSearchCustomRepository {

  private final JdbcTemplate jdbcTemplate;

  public SoftAskSearchCustomRepositoryImpl(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public Page<SoftAskWithDetail> findMany(
    Double latitude,
    Double longitude,
    Double radiusKm,
    Pageable pageable) {

    List<Object> dataParams = new ArrayList<>();
    List<Object> countParams = new ArrayList<>();

    StringBuilder dataQueryBuilder = new StringBuilder("""
        SELECT
            s.soft_ask_id AS softAskId,
            s.title AS title,
            s.description AS description,
            s.tags AS tags,
            s.link AS link,
            s.parent_id AS parentId,
            s.parent_title AS parentTitle,

            s.parent_type AS parentType,
            s.visibility AS visibility,
            s.status AS status,
            s.location_visibility AS locationVisibility,
            s.mood_tag AS moodTag,

            s.chat_space_id AS chatSpaceId,
            s.poll_id AS pollId,
            s.stream_id AS streamId,
            s.author_id AS authorId,

            s.geohash AS geohash,
            s.geohash_prefix AS geohashPrefix,
            s.is_deleted AS deleted,
            s.is_visible AS visible,

            s.bookmark_count AS bookmarkCount,
            s.participant_count AS participantCount,
            s.reply_count AS replyCount,
            s.share_count AS shareCount,
            s.vote_count AS voteCount,

            s.latitude AS latitude,
            s.longitude AS longitude,
            s.slug AS slug,

            s.created_on AS createdOn,
            s.updated_on AS updatedOn,

            sau.id AS participantId,
            sau.username AS username,
            sau.display_name AS displayName,
            sau.avatar AS avatar,

            CASE
                WHEN COALESCE(?, 0) != 0 AND COALESCE(?, 0) != 0
                THEN ST_Distance(s.location, ST_SetSRID(ST_MakePoint(?, ?), 4326))
                ELSE NULL
            END AS distance
        FROM soft_ask s
        LEFT JOIN soft_ask_participant_detail sau ON s.soft_ask_id = sau.soft_ask_id
        WHERE s.is_deleted = false
    """);

    // Bind latitude/longitude for COALESCE and ST_Distance
    dataParams.add(latitude);
    dataParams.add(longitude);
    dataParams.add(longitude);
    dataParams.add(latitude);

    StringBuilder countQueryBuilder = new StringBuilder("""
        SELECT COUNT(*)
        FROM soft_ask s
        WHERE s.is_deleted = false
    """);

    if (latitude != null && longitude != null) {
      String locationCondition = " AND ST_DWithin(s.location, ST_SetSRID(ST_MakePoint(?, ?), 4326), ? * 1000) ";
      dataQueryBuilder.append(locationCondition);
      countQueryBuilder.append(locationCondition);

      countParams.add(longitude);
      countParams.add(latitude);
      countParams.add(radiusKm);

      dataParams.add(longitude);
      dataParams.add(latitude);
      dataParams.add(radiusKm);
    }

    // Total count
    Long total = jdbcTemplate.queryForObject(countQueryBuilder.toString(), Long.class, countParams.toArray());

    // ORDER BY using COALESCE
    dataQueryBuilder.append("""
        ORDER BY
        CASE
            WHEN COALESCE(?, 0) != 0 AND COALESCE(?, 0) != 0
            THEN ST_Distance(s.location, ST_SetSRID(ST_MakePoint(?, ?), 4326))
            ELSE EXTRACT(EPOCH FROM s.created_on) * -1
        END,
        s.soft_ask_id
    """);

    dataParams.add(latitude);
    dataParams.add(longitude);
    dataParams.add(longitude);
    dataParams.add(latitude);

    // Pagination
    dataQueryBuilder.append(" LIMIT ? OFFSET ?");
    dataParams.add(pageable.getPageSize());
    dataParams.add(pageable.getOffset());

    List<SoftAskWithDetail> results = jdbcTemplate.query(
      dataQueryBuilder.toString(),
      new SoftAskWithDetailMapper(),
      dataParams.toArray()
    );

    return new PageImpl<>(results, pageable, total != null ? total : 0);
  }

  @Override
  public Page<SoftAskWithDetail> findByAuthor(Long authorId, Pageable pageable) {

    List<Object> params = new ArrayList<>();
    params.add(authorId);

    // Data query
    String dataQuery = """
      SELECT
          sa.soft_ask_id        AS softAskId,
          sa.title              AS title,
          sa.description        AS description,
          sa.tags               AS tags,
          sa.link               AS link,
          sa.parent_id          AS parentId,
          sa.parent_title       AS parentTitle,
  
          sa.parent_type        AS parentType,
          sa.visibility         AS visibility,
          sa.status             AS status,
          sa.location_visibility AS locationVisibility,
          sa.mood_tag           AS moodTag,
  
          sa.chat_space_id      AS chatSpaceId,
          sa.poll_id            AS pollId,
          sa.stream_id          AS streamId,
          sa.author_id          AS authorId,
  
          sa.geohash            AS geohash,
          sa.geohash_prefix     AS geohashPrefix,
          sa.is_deleted         AS deleted,
          sa.is_visible         AS visible,
  
          sa.bookmark_count     AS bookmarkCount,
          sa.participant_count  AS participantCount,
          sa.reply_count        AS replyCount,
          sa.share_count        AS shareCount,
          sa.vote_count         AS voteCount,
  
          sa.latitude           AS latitude,
          sa.longitude          AS longitude,
          sa.slug               AS slug,
  
          sa.created_on         AS createdOn,
          sa.updated_on         AS updatedOn,
  
          sau.id                AS participantId,
          sau.username          AS username,
          sau.display_name      AS displayName,
          sau.avatar            AS avatar,
          NULL                  AS distance
      FROM soft_ask sa
      JOIN soft_ask_participant_detail sau
           ON sa.soft_ask_id = sau.soft_ask_id
      WHERE sa.author_id = ?
      ORDER BY sa.updated_on DESC
      LIMIT ? OFFSET ?
      """;

    params.add(pageable.getPageSize());
    params.add(pageable.getOffset());

    List<SoftAskWithDetail> results = jdbcTemplate.query(
      dataQuery,
      new SoftAskWithDetailMapper(),
      params.toArray()
    );

    // Count query
    String countQuery = """
            SELECT COUNT(*)
            FROM soft_ask sa
            WHERE sa.author_id = ?
        """;

    Long total = jdbcTemplate.queryForObject(countQuery, Long.class, authorId);

    return new PageImpl<>(results, pageable, total != null ? total : 0);
  }

}