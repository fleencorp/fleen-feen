package com.fleencorp.feen.softask.repository.reply;

import com.fleencorp.feen.softask.model.domain.SoftAskReply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SoftAskReplyRepository extends JpaRepository<SoftAskReply, Long> {

  @Query(value = """
    SELECT sar FROM SoftAskReply sar
    WHERE
      sar.softAskId = :softAskParentId AND
      sar.softAskReplyId = :softAskReplyId
  """)
  Optional<SoftAskReply> findBySoftAskAndParentReply(
    @Param("softAskParentId") Long softAskParentId,
    @Param("softAskReplyId") Long softAskReplyId);

  @Query(value = """
    SELECT sar FROM SoftAskReply sar
    WHERE sar.softAskId = :softAskId AND
    sar.parentReplyId = :softAskReplyParentId AND
    sar.softAskReplyId = :softAskReplyId
  """)
  Optional<SoftAskReply> findBySoftAskAndReplyParentAndReply(Long softAskParentId, Long softAskReplyParentId, Long softAskReplyId);

  @Query(value = "SELECT sar FROM SoftAskReply sar WHERE sar.softAskId = :softAskId AND sar.softAskReplyId = :softAskReplyId")
  Optional<SoftAskReply> findBySoftAskAndReply(@Param("softAskId") Long softAskId, @Param("softAskReplyId") Long softAskReplyId);

  @Modifying
  @Query(value = """
    UPDATE SoftAskReply sar SET sar.voteCount = sar.voteCount + 1
    WHERE sar.softAskId = :softAskId AND
    sar.softAskReplyId = :softAskReplyId
  """)
  void incrementVoteCount(@Param("softAskId") Long softAskId, @Param("softAskReplyId") Long softAskReplyId);

  @Modifying
  @Query(value = """
    UPDATE SoftAskReply sar SET sar.voteCount = sar.voteCount - 1
    WHERE sar.softAskId = :softAskId AND
    sar.softAskReplyId = :softAskReplyId
  """)
  void decrementVoteCount(@Param("softAskId") Long softAskId, @Param("softAskReplyId") Long softAskReplyId);

  @Query("SELECT sar.voteCount FROM SoftAskReply sar WHERE sar.softAskId = :softAskId AND sar.softAskReplyId = :softAskReplyId")
  Integer getVoteCount(@Param("softAskId") Long softAskId, @Param("softAskReplyId") Long softAskReplyId);

  @Modifying
  @Query(value = """
    UPDATE SoftAskReply sar SET sar.childReplyCount = sar.childReplyCount + 1
      WHERE sar.softAskId = :softAskId AND
      sar.softAskReplyId = :softAskReplyId
  """)
  void incrementReplyChildReplyCount(@Param("softAskId") Long softAskId, @Param("softAskReplyId") Long softAskReplyId);

  @Query(value = """
    SELECT sar.childReplyCount FROM SoftAskReply sar
    WHERE sar.softAskId = :softAskId AND
    sar.softAskReplyId = :softAskReplyId
  """)
  Integer getReplyChildReplyCount(@Param("softAskId") Long softAskId, @Param("softAskReplyId") Long softAskReplyId);

  @Modifying
  @Query(value = """
    UPDATE soft_ask_reply
    SET bookmark_count = bookmark_count - 1
    WHERE soft_ask_id = :softAskId
    AND soft_ask_reply_id = :softAskReplyId
    """,
    nativeQuery = true)
  void decrementAndGetBookmarkCount(@Param("softAskId") Long softAskId, @Param("softAskReplyId") Long softAskReplyId);

  @Modifying
  @Query(value = """
    UPDATE soft_ask_reply
    SET bookmark_count = bookmark_count + 1
    WHERE
      soft_ask_id = :softAskId AND
      soft_ask_reply_id = :softAskReplyId
    """,
    nativeQuery = true)
  void incrementAndBookmarkCount(@Param("softAskId") Long softAskId, @Param("softAskReplyId") Long softAskReplyId);

  @Query(value = """
    SELECT bookmark_count
    FROM soft_ask_reply
    WHERE
      soft_ask_id = :softAskId AND
      soft_ask_reply_id = :softAskReplyId
    """,
    nativeQuery = true)
  Integer getBookmarkCount(@Param("softAskId") Long softAskId, @Param("softAskReplyId") Long softAskReplyId);
}
