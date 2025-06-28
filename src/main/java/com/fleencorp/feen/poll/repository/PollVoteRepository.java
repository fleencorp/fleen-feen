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
    SELECT po.poll_option_id AS optionId,
           po.option_text AS optionText,
           COUNT(pv.vote_id) AS voteCount,
           (SELECT COUNT(*)
             FROM poll_vote pv2
             WHERE pv2.poll_id = :pollId) AS totalVotes
    FROM poll_option po
    LEFT JOIN poll_vote pv ON po.poll_option_id = pv.option_id
    WHERE po.poll_id = :pollId
    GROUP BY po.poll_option_id, po.option_text
    """, nativeQuery = true)
  List<PollVoteAggregate> findVoteAggregatesByPollId(@Param("pollId") Long pollId);

}
