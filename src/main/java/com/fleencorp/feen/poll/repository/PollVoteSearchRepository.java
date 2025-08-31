package com.fleencorp.feen.poll.repository;

import com.fleencorp.feen.poll.model.domain.PollVote;
import com.fleencorp.feen.user.model.domain.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PollVoteSearchRepository extends JpaRepository<PollVote, Long> {

  @Query("SELECT pv FROM PollVote pv WHERE pv.pollVoteId IS NOT NULL ORDER BY pv.updatedOn DESC")
  Page<PollVote> findMany(Pageable pageable);

  @Query("SELECT DISTINCT pv.voter FROM PollVote pv WHERE pv.pollId = :pollId")
  Page<Member> findDistinctVotersByPollId(@Param("pollId") Long pollId, Pageable pageable);

  @Query("SELECT DISTINCT pv.voter FROM PollVote pv WHERE pv.pollId = :pollId AND pv.pollOptionId = :optionId")
  Page<Member> findDistinctVotersByPollIdAndOptionId(@Param("pollId") Long pollId, @Param("optionId") Long optionId, Pageable pageable);
}
