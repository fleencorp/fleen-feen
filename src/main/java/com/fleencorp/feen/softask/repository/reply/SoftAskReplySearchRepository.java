package com.fleencorp.feen.softask.repository.reply;

import com.fleencorp.feen.softask.model.domain.SoftAskReply;
import com.fleencorp.feen.softask.model.projection.SoftAskReplyWithDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SoftAskReplySearchRepository extends JpaRepository<SoftAskReply, Long> {

  @Query("SELECT sar FROM SoftAskReply sar WHERE sar.softAskId = :softAskId AND sar.softAskReplyId = :softAskReplyId")
  Optional<SoftAskReply> findBySoftAskAndSoftAskReply(Long softAskId, Long softAskReplyId);

  @Query(value = """
    SELECT new com.fleencorp.feen.softask.model.projection.SoftAskReplyWithDetail(sar, sau)
    FROM SoftAskReply sar
    JOIN SoftAskUsername sau
    ON
      sar.softAskId = sau.softAskId AND
      sar.authorId = sau.userId AND
      sar.parentReplyId = NULL
    WHERE
      sar.softAskId = :softAskId AND
      sar.deleted = false
    ORDER BY sar.updatedOn DESC
  """)
  Page<SoftAskReplyWithDetail> findBySoftAsk(@Param("softAskId") Long softAskId, Pageable pageable);

  @Query(value = """
    SELECT new com.fleencorp.feen.softask.model.projection.SoftAskReplyWithDetail(sar, sau)
    FROM SoftAskReply sar
    JOIN SoftAskUsername sau
    ON
      sar.softAskId = sau.softAskId AND
      sar.authorId = sau.userId
    WHERE
      sar.parentReplyId = :parentReplyId AND
      sar.deleted = false
    ORDER BY sar.updatedOn DESC
  """)
  Page<SoftAskReplyWithDetail> findBySoftAskAndParentReply(@Param("softAskId") Long softAskId, @Param("parentReplyId") Long parentReplyId, Pageable pageable);


  @Query(value = """
    SELECT new com.fleencorp.feen.softask.model.projection.SoftAskReplyWithDetail(sar, sau)
    FROM SoftAskReply sar
    JOIN SoftAskUsername sau
    ON
      sar.softAskId = sau.softAskId AND
      sar.authorId = sau.userId
    WHERE
      sar.authorId = :authorId AND
      sar.deleted = false
    ORDER BY sar.updatedOn DESC
  """)
  Page<SoftAskReplyWithDetail> findByAuthor(@Param("authorId") Long authorId, Pageable pageable);

}
