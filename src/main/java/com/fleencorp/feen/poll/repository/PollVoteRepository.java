package com.fleencorp.feen.poll.repository;

import com.fleencorp.feen.poll.model.domain.PollVote;
import com.fleencorp.feen.poll.model.projection.PollVoteAggregate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface PollVoteRepository extends JpaRepository<PollVote, Long> {

  @Query(value =
  """
    SELECT pv FROM PollVote pv
    WHERE pv.pollId IN (:pollIds) AND pv.voterId = :memberId
  """)
  List<PollVote> findByPollIdsAndMemberId(@Param("pollIds") Collection<Long> pollIds, @Param("memberId") Long memberId);

  @Query("SELECT pv FROM PollVote pv WHERE pv.pollId = :pollId AND pv.voterId = :memberId")
  List<PollVote> findManyByPollIdAndMemberId(@Param("pollId") Long pollId, @Param("memberId") Long memberId);

  @Modifying
  @Query("DELETE FROM PollVote pv WHERE pv.poll.pollId = :pollId AND pv.voterId = :memberId")
  void deleteByPollIdAndMemberId(@Param("pollId") Long pollId, @Param("memberId") Long memberId);

  @Query(value =
    """
    SELECT po.pollOptionId AS optionId,
           po.optionText AS optionText,
           COUNT(pv.pollVoteId) AS voteCount,
           (SELECT COUNT(*)
             FROM PollVote pv2
             WHERE pv2.pollId = :pollId) AS totalVotes
    FROM PollOption po
    LEFT JOIN PollVote pv ON po.pollOptionId = pv.pollOptionId
    WHERE po.pollId = :pollId
    GROUP BY po.pollOptionId, po.optionText
    """)
  List<PollVoteAggregate> findVoteAggregatesByPollId(@Param("pollId") Long pollId);

}
