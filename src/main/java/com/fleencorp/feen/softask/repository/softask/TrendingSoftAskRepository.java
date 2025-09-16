package com.fleencorp.feen.softask.repository.softask;

import com.fleencorp.feen.softask.model.domain.SoftAsk;
import com.fleencorp.feen.softask.model.projection.SoftAskWithDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TrendingSoftAskRepository extends JpaRepository<SoftAsk, Long> {

  @Query(value = """
    SELECT s.*,
           sau.id AS participant_id,
           sau.username,
           sau.display_name,
           sau.avatar,
           CASE
               WHEN :latitude IS NOT NULL AND :longitude IS NOT NULL
               THEN ST_Distance(
                    s.location,
                    ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326)
               )
               ELSE NULL
           END AS distance,
           (
               (s.vote_count * 2 + s.reply_count + s.bookmark_count + s.share_count)
               / GREATEST(EXTRACT(EPOCH FROM (NOW() - s.created_on)) / 3600, 1)
           ) AS trending_score
    FROM soft_ask s
    LEFT JOIN soft_ask_participant_detail sau
           ON s.soft_ask_id = sau.soft_ask_id
    WHERE s.is_deleted = false
      AND (
          :latitude IS NULL OR :longitude IS NULL
          OR ST_DWithin(
                s.location,
                ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326),
                :radius * 1000
          )
      )
    ORDER BY trending_score DESC
    """,
    countQuery = """
    SELECT COUNT(*)
    FROM soft_ask s
    LEFT JOIN soft_ask_participant_detail sau
           ON s.soft_ask_id = sau.soft_ask_id
    WHERE s.is_deleted = false
      AND (
          :latitude IS NULL OR :longitude IS NULL
          OR ST_DWithin(
                s.location,
                ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326),
                :radius * 1000
          )
      )
    """,
    nativeQuery = true)
  Page<SoftAskWithDetail> findTrendingNearby(
    @Param("latitude") Double latitude,
    @Param("longitude") Double longitude,
    @Param("radius") Double radiusKm,
    Pageable pageable
  );

}
