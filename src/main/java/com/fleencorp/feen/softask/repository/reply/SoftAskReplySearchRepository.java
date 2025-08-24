package com.fleencorp.feen.softask.repository.reply;

import com.fleencorp.feen.softask.model.domain.SoftAskReply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SoftAskReplySearchRepository extends JpaRepository<SoftAskReply, Long> {

  @Query(value = """
    SELECT sar FROM SoftAskReply sar
    WHERE sar.softAskId = :softAskId
    AND sar.parentReplyId = NULL
    ORDER BY sar.updatedOn DESC
  """)
  Page<SoftAskReply> findBySoftAsk(@Param("softAskId") Long softAskId, Pageable pageable);

  @Query(value = """
    SELECT sar FROM SoftAskReply sar
    WHERE sar.softAskId = :softAskId AND
    sar.parentReplyId = :parentReplyId
    ORDER BY sar.updatedOn DESC
  """)
  Page<SoftAskReply> findBySoftAskAndParentReply(@Param("softAskId") Long softAskId, @Param("parentReplyId") Long parentReplyId, Pageable pageable);

  @Query("SELECT sar FROM SoftAskReply sar WHERE sar.softAskId = :softAskId AND sar.softAskReplyId = :softAskReplyId")
  Optional<SoftAskReply> findBySoftAskAndSoftAskReply(Long softAskId, Long softAskReplyId);

  @Query("SELECT sar FROM SoftAskReply sar WHERE sar.authorId = :authorId AND sar.deleted = false ORDER BY sar.updatedOn DESC")
  Page<SoftAskReply> findByAuthor(@Param("authorId") Long authorId, Pageable pageable);
}
