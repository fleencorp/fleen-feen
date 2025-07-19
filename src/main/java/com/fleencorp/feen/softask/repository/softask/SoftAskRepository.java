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
  @Query("UPDATE SoftAsk sa SET sa.answerCount = sa.answerCount + 1 WHERE sa.softAskId = :softAskId")
  void incrementAnswerCount(@Param("softAskId") Long softAskId);

  @Query("SELECT sa.answerCount FROM SoftAsk sa WHERE sa.softAskId = :softAskId")
  Integer getAnswerCount(@Param("softAskId") Long softAskId);
}
