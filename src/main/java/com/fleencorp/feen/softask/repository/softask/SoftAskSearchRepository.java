package com.fleencorp.feen.softask.repository.softask;

import com.fleencorp.feen.softask.model.domain.SoftAsk;
import com.fleencorp.feen.softask.model.projection.SoftAskWithDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SoftAskSearchRepository extends JpaRepository<SoftAsk, Long> {

  @Query(value = """
    SELECT new com.fleencorp.feen.softask.model.projection.SoftAskWithDetail(sa, sau)
    FROM SoftAsk sa
    JOIN SoftAskParticipantDetail sau
    ON
      sa.softAskId = sau.softAskId
    WHERE
      sa.softAskId IS NOT NULL
    ORDER BY sa.updatedOn DESC
  """)
  Page<SoftAskWithDetail> findMany(Pageable pageable);

  @Query(value = """
    SELECT new com.fleencorp.feen.softask.model.projection.SoftAskWithDetail(sa, sau)
    FROM SoftAsk sa
    JOIN SoftAskParticipantDetail sau
    ON
      sa.softAskId = sau.softAskId
    WHERE
      sa.authorId = :authorId
    ORDER BY sa.updatedOn DESC
  """)
  Page<SoftAskWithDetail> findByAuthor(@Param("authorId") Long authorId, Pageable pageable);

}
