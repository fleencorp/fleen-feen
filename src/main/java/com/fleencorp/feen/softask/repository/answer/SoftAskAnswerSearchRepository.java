package com.fleencorp.feen.softask.repository.answer;

import com.fleencorp.feen.softask.model.domain.SoftAskAnswer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SoftAskAnswerSearchRepository extends JpaRepository<SoftAskAnswer, Long> {

  @Query("SELECT saa FROM SoftAskAnswer saa WHERE saa.softAskId = :softAskId ORDER BY saa.updatedOn DESC")
  Page<SoftAskAnswer> findBySoftAsk(@Param("softAskId") Long softAskId, Pageable pageable);

  @Query("SELECT saa FROM SoftAskAnswer saa WHERE saa.authorId = :authorId ORDER BY saa.updatedOn DESC")
  Page<SoftAskAnswer> findByAuthor(@Param("authorId") Long authorId, Pageable pageable);
}
