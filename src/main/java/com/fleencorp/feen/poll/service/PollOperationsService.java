package com.fleencorp.feen.poll.service;

import com.fleencorp.feen.poll.model.domain.Poll;
import com.fleencorp.feen.poll.model.domain.PollVote;
import com.fleencorp.feen.poll.model.holder.PollOptionEntriesHolder;
import com.fleencorp.feen.poll.model.holder.PollVoteAggregateHolder;
import com.fleencorp.feen.poll.model.holder.PollVoteEntriesHolder;
import com.fleencorp.feen.user.model.domain.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.Optional;

public interface PollOperationsService {

  Poll save(Poll poll);

  void saveAll(Collection<PollVote> pollVotes);

  Poll findPoll(Long pollId);

  Optional<Poll> findById(Long pollId);

  Page<Poll> findMany(Pageable pageable);

  Page<Member> findVoters(Long pollId, Pageable pageable);

  Page<Member> findVoters(Long pollId, Long pollOptionId, Pageable pageable);

  Page<Poll> findByAuthor(Long authorId, Pageable pageable);

  Page<Poll> findByStream(Long streamId, Pageable pageable);

  Page<Poll> findByChatSpace(Long chatSpaceId, Pageable pageable);

  void incrementPollOptionTotalEntries(Long pollId, Collection<Long> optionIds);

  void decrementPollOptionTotalEntries(Long pollId, Collection<Long> optionIds);

  PollOptionEntriesHolder findOptionEntries(Long pollId, Collection<Long> optionIds);

  PollVoteEntriesHolder findVotesByPollIdsAndMemberId(Collection<Long> pollIds, Long memberId);

  PollVoteEntriesHolder findVotesByPollIdAndMemberId(Long pollId, Long memberId);

  PollVoteAggregateHolder findPollVoteAggregate(Long pollId);

  void deleteVoteByPollIdAndMemberId(Long pollId, Long memberId);

  Integer updateBookmarkCount(Long streamId, boolean bookmarked);

  Integer updateLikeCount(Long chatSpaceId, boolean isLiked);
}

