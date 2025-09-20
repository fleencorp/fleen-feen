package com.fleencorp.feen.softask.repository.vote;

import com.fleencorp.feen.softask.model.domain.SoftAskVote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SoftAskVoteRepository extends JpaRepository<SoftAskVote, Long> {

  @Query(value = """
    SELECT sav FROM SoftAskVote sav
    WHERE
      sav.memberId = :memberId AND
      sav.softAskId = :softAskId AND
      sav.parentId = :softAskId AND
      sav.softAskReplyId is NULL
  """)
  Optional<SoftAskVote> findByMemberAndSoftAsk(
    @Param("memberId") Long memberId,
    @Param("softAskId") Long softAskId
  );

  @Query(value = """
    SELECT sav FROM SoftAskVote sav
    WHERE
      sav.memberId = :memberId AND
      sav.softAskId = :softAskId AND
      sav.softAskReplyId = :softAskReplyId AND
      sav.parentId = :softAskReplyId
  """)
  Optional<SoftAskVote> findByMemberAndSoftAskAndSoftAskReply(
    @Param("memberId") Long memberId,
    @Param("softAskId") Long softAskId,
    @Param("softAskReplyId") Long softAskReplyId
  );

  @Query("SELECT COUNT(v) FROM SoftAskVote v WHERE v.softAsk.authorId = :memberId")
  int countVotesOnMySoftAsks(@Param("memberId") Long memberId);

  @Query("SELECT COUNT(v) FROM SoftAskVote v WHERE v.softAskReply.authorId = :memberId")
  int countVotesOnMyReplies(@Param("memberId") Long memberId);

}
