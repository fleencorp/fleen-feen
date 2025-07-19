package com.fleencorp.feen.softask.repository.answer;

import com.fleencorp.feen.softask.model.domain.SoftAskAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SoftAskAnswerRepository extends JpaRepository<SoftAskAnswer, Long> {

  @Modifying
  @Query("UPDATE SoftAskAnswer saa SET saa.voteCount = saa.voteCount + 1 WHERE saa.softAskAnswerId = :softAnswerId")
  void incrementVoteCount(@Param("softAnswerId") Long softAnswerId);

  @Modifying
  @Query("UPDATE SoftAskAnswer saa SET saa.voteCount = saa.voteCount - 1 WHERE saa.softAskAnswerId = :softAnswerId")
  void decrementVoteCount(@Param("softAnswerId") Long softAnswerId);

  @Query("SELECT saa.voteCount FROM SoftAskAnswer saa WHERE saa.softAskAnswerId = :softAnswerId")
  Integer getVoteCount(@Param("softAnswerId") Long softAnswerId);

  @Modifying
  @Query("UPDATE SoftAskAnswer saa SET saa.replyCount = saa.replyCount + 1 WHERE saa.softAskAnswerId = :softAskAnswerId")
  void incrementReplyCount(@Param("softAskAnswerId") Long softAskAnswerId);

  @Query("SELECT saa.replyCount FROM SoftAskAnswer saa WHERE saa.softAskAnswerId = :softAskAnswerId")
  Integer getReplyCount(@Param("softAskAnswerId") Long softAskAnswerId);
}
