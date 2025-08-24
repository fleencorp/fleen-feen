package com.fleencorp.feen.softask.repository.softask;

import com.fleencorp.feen.softask.model.domain.SoftAsk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SoftAskRepository extends JpaRepository<SoftAsk, Long> {

  @Modifying
  @Query("UPDATE SoftAsk sa SET sa.voteCount = sa.voteCount + 1 WHERE sa.softAskId = :softAskId")
  void incrementVoteCount(@Param("softAskId") Long softAskId);

  @Modifying
  @Query("UPDATE SoftAsk sa SET sa.voteCount = sa.voteCount - 1 WHERE sa.softAskId = :softAskId")
  void decrementVoteCount(@Param("softAskId") Long softAskId);

  @Query("SELECT sa.voteCount FROM SoftAsk sa WHERE sa.softAskId = :softAskId")
  Integer getVoteCount(@Param("softAskId") Long softAskId);

  @Modifying
  @Query("UPDATE SoftAsk sa SET sa.replyCount = sa.replyCount + 1 WHERE sa.softAskId = :softAskId")
  void incrementReplyCount(@Param("softAskId") Long softAskId);

  @Query("SELECT sa.replyCount FROM SoftAsk sa WHERE sa.softAskId = :softAskId")
  Integer getReplyCount(@Param("softAskId") Long softAskId);

  @Modifying
  @Query(value = "UPDATE soft_ask SET bookmark_count = bookmark_count + 1 WHERE soft_ask_id = :softAskId", nativeQuery = true)
  void incrementAndBookmarkCount(@Param("softAskId") Long softAskId);

  @Modifying
  @Query(value = "UPDATE soft_ask SET bookmark_count = bookmark_count - 1 WHERE soft_ask_id = :softAskId", nativeQuery = true)
  void decrementAndGetBookmarkCount(@Param("softAskId") Long softAskId);

  @Query(value = "SELECT bookmark_count FROM soft_ask WHERE soft_ask_id = :softAskId", nativeQuery = true)
  Integer getBookmarkCount(@Param("softAskId") Long softAskId);
}
