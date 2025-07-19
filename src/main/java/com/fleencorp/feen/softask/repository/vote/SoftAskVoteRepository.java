package com.fleencorp.feen.softask.repository.vote;

import com.fleencorp.feen.softask.model.domain.SoftAskVote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;


public interface SoftAskVoteRepository extends JpaRepository<SoftAskVote, Long> {

  @Query("SELECT sav FROM SoftAskVote sav WHERE sav.memberId = :memberId AND sav.softAskId = :softAskId")
  Optional<SoftAskVote> findByMemberAndSoftAsk(@Param("memberId") Long memberId, @Param("softAskId") Long softAskId);

  @Query("SELECT sav FROM SoftAskVote sav WHERE sav.memberId = :memberId AND sav.softAskAnswerId = :softAskAnswerId")
  Optional<SoftAskVote> findByMemberAndSoftAskAnswer(@Param("memberId") Long memberId, @Param("softAskAnswerId") Long softAskAnswerId);

  @Query("SELECT sav FROM SoftAskVote sav WHERE sav.memberId = :memberId AND sav.softAskReplyId = :softAskReplyId")
  Optional<SoftAskVote> findByMemberAndSoftAskReply(@Param("memberId") Long memberId, @Param("softAskReplyId") Long softAskReplyId);

  @Query("SELECT COUNT(v) FROM SoftAskVote v WHERE v.softAsk.authorId = :memberId")
  int countVotesOnMySoftAsks(@Param("memberId") Long memberId);

  @Query("SELECT COUNT(v) FROM SoftAskVote v WHERE v.softAskAnswer.authorId = :memberId")
  int countVotesOnMyAnswers(@Param("memberId") Long memberId);

  @Query("SELECT COUNT(v) FROM SoftAskVote v WHERE v.softAskReply.authorId = :memberId")
  int countVotesOnMyReplies(@Param("memberId") Long memberId);

}
