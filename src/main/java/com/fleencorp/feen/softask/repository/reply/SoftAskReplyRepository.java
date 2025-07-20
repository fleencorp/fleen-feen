package com.fleencorp.feen.softask.repository.reply;

import com.fleencorp.feen.softask.model.domain.SoftAskReply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SoftAskReplyRepository extends JpaRepository<SoftAskReply, Long> {

  @Modifying
  @Query("UPDATE SoftAskReply sar SET sar.voteCount = sar.voteCount + 1 WHERE sar.softAskReplyId = :softAskReplyId")
  void incrementVoteCount(@Param("softAskReplyId") Long softAskReplyId);

  @Modifying
  @Query("UPDATE SoftAskReply sar SET sar.voteCount = sar.voteCount - 1 WHERE sar.softAskReplyId = :softAskReplyId")
  void decrementVoteCount(@Param("softAskReplyId") Long softAskReplyId);

  @Query("SELECT sar.voteCount FROM SoftAskReply sar WHERE sar.softAskReplyId = :softAskReplyId")
  Integer getVoteCount(@Param("softAskReplyId") Long softAskReplyId);
}
