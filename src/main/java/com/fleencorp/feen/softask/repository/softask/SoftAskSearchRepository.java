package com.fleencorp.feen.softask.repository.softask;

import com.fleencorp.feen.softask.model.domain.SoftAsk;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SoftAskSearchRepository extends JpaRepository<SoftAsk, Long> {

  @Query("SELECT sa FROM SoftAsk sa WHERE sa.softAskId IS NOT NULL ORDER BY sa.updatedOn DESC")
  Page<SoftAsk> findMany(Pageable pageable);

  @Query("SELECT sa FROM SoftAskAnswer sa WHERE sa.authorId = :authorId ORDER BY sa.updatedOn DESC")
  Page<SoftAsk> findByAuthor(@Param("authorId") Long authorId, Pageable pageable);
}
