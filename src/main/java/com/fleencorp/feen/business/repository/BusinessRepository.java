package com.fleencorp.feen.business.repository;

import com.fleencorp.feen.business.constant.BusinessStatus;
import com.fleencorp.feen.business.model.domain.Business;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BusinessRepository extends JpaRepository<Business, Long> {

  @Query(value = """
    SELECT b FROM Business b
    WHERE
      LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%')) AND
      b.status = :status
    ORDER BY b.updatedOn DESC
  """)
  Page<Business> findByTitleContainingIgnoreCase(@Param("title") String title, @Param("status") BusinessStatus status, Pageable pageable);

  @Query(value = """
    SELECT b FROM Business b
    WHERE
      b.businessId IS NOT NULL AND
      b.status = :status
    ORDER BY b.updatedOn DESC
  """)
  Page<Business> findMany(@Param("status") BusinessStatus status, Pageable pageable);
}
