package com.fleencorp.feen.softask.repository.reply;

import com.fleencorp.feen.softask.model.domain.SoftAskReply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SoftAskReplySearchRepository extends JpaRepository<SoftAskReply, Long> {

  @Query("SELECT sar FROM SoftAskReply sar WHERE sar.softAnswerId = :softAskAnswerId ORDER BY sar.updatedOn DESC")
  Page<SoftAskReply> findBySoftAskAnswer(@Param("softAskAnswerId") Long softAskId, Pageable pageable);

  @Query("SELECT sar FROM SoftAskReply sar WHERE sar.authorId = :authorId AND sar.deleted = false ORDER BY sar.updatedOn DESC")
  Page<SoftAskReply> findByAuthor(@Param("authorId") Long authorId, Pageable pageable);
}
