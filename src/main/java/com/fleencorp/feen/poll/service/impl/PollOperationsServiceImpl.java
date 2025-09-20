package com.fleencorp.feen.poll.service.impl;

import com.fleencorp.feen.poll.model.domain.Poll;
import com.fleencorp.feen.poll.model.domain.PollVote;
import com.fleencorp.feen.poll.model.holder.PollOptionEntriesHolder;
import com.fleencorp.feen.poll.model.holder.PollVoteAggregateHolder;
import com.fleencorp.feen.poll.model.holder.PollVoteEntriesHolder;
import com.fleencorp.feen.poll.model.projection.PollOptionEntry;
import com.fleencorp.feen.poll.model.projection.PollVoteAggregate;
import com.fleencorp.feen.poll.repository.*;
import com.fleencorp.feen.poll.service.PollOperationsService;
import com.fleencorp.feen.poll.service.PollOtherService;
import com.fleencorp.feen.user.model.domain.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class PollOperationsServiceImpl implements PollOperationsService {

  private final PollOtherService pollOtherService;
  private final PollRepository pollRepository;
  private final PollManagementRepository pollManagementRepository;
  private final PollOptionRepository pollOptionRepository;
  private final PollVoteSearchRepository pollVoteSearchRepository;
  private final PollVoteRepository pollVoteRepository;

  public PollOperationsServiceImpl(
      final PollOtherService pollOtherService,
      final PollRepository pollRepository,
      final PollManagementRepository pollManagementRepository,
      final PollOptionRepository pollOptionRepository,
      final PollVoteSearchRepository pollVoteSearchRepository,
      final PollVoteRepository pollVoteRepository) {
    this.pollOtherService = pollOtherService;
    this.pollRepository = pollRepository;
    this.pollManagementRepository = pollManagementRepository;
    this.pollOptionRepository = pollOptionRepository;
    this.pollVoteSearchRepository = pollVoteSearchRepository;
    this.pollVoteRepository = pollVoteRepository;
  }

  @Override
  @Transactional
  public Poll save(final Poll poll) {
    return pollRepository.save(poll);
  }

  @Override
  @Transactional
  public void saveAll(final Collection<PollVote> pollVotes) {
    pollVoteRepository.saveAll(pollVotes);
  }

  @Override
  public Poll findPoll(Long pollId) {
    return pollOtherService.findPollById(pollId);
  }

  @Override
  public Optional<Poll> findById(final Long pollId) {
    return pollRepository.findById(pollId);
  }

  @Override
  public Page<Poll> findMany(final Pageable pageable) {
    return pollRepository.findMany(pageable);
  }


  @Override
  public Page<Member> findVoters(final Long pollId, final Pageable pageable) {
    return pollVoteSearchRepository.findDistinctVotersByPollId(pollId, pageable);
  }

  @Override
  public Page<Member> findVoters(final Long pollId, final Long pollOptionId, final Pageable pageable) {
    return pollVoteSearchRepository.findDistinctVotersByPollIdAndOptionId(pollId, pollOptionId, pageable);
  }

  @Override
  public Page<Poll> findByAuthor(final Long authorId, final Pageable pageable) {
    return pollRepository.findByAuthor(authorId, pageable);
  }

  @Override
  public Page<Poll> findByStream(final Long streamId, final Pageable pageable) {
    return pollRepository.findByStream(streamId, pageable);
  }

  @Override
  public Page<Poll> findByChatSpace(final Long chatSpaceId, final Pageable pageable) {
    return pollRepository.findByChatSpace(chatSpaceId, pageable);
  }

  @Override
  @Transactional
  public void incrementPollOptionTotalEntries(final Long pollId, final Collection<Long> optionIds) {
    pollOptionRepository.incrementTotalEntries(pollId, optionIds);
  }

  @Override
  @Transactional
  public void decrementPollOptionTotalEntries(final Long pollId, final Collection<Long> optionIds) {
    pollOptionRepository.decrementTotalEntries(pollId, optionIds);
  }

  @Override
  public PollOptionEntriesHolder findOptionEntries(final Long pollId, final Collection<Long> optionIds) {
    final List<PollOptionEntry> pollOptionEntries = pollOptionRepository.findOptionEntries(pollId, optionIds);
    return PollOptionEntriesHolder.of(pollOptionEntries);
  }

  @Override
  public PollVoteEntriesHolder findVotesByPollIdsAndMemberId(final Collection<Long> pollIds, final Long memberId) {
    final List<PollVote> pollVotes = pollVoteRepository.findByPollIdsAndMemberId(pollIds, memberId);
    return PollVoteEntriesHolder.of(pollVotes);
  }

  @Override
  public PollVoteEntriesHolder findVotesByPollIdAndMemberId(final Long pollId, final Long memberId) {
    final List<PollVote> pollVotes = pollVoteRepository.findManyByPollIdAndMemberId(pollId, memberId);
    return PollVoteEntriesHolder.of(pollVotes);
  }

  @Override
  public PollVoteAggregateHolder findPollVoteAggregate(final Long pollId) {
    final List<PollVoteAggregate> pollVoteAggregates = pollVoteRepository.findVoteAggregatesByPollId(pollId);
    return PollVoteAggregateHolder.of(pollVoteAggregates);
  }

  @Override
  @Transactional
  public void deleteVoteByPollIdAndMemberId(final Long pollId, final Long memberId) {
    pollVoteRepository.deleteByPollIdAndMemberId(pollId, memberId);
  }

  private int incrementAndGetBookmarkCount(final Long streamId) {
    pollManagementRepository.incrementAndGetBookmarkCount(streamId);
    return pollManagementRepository.getBookmarkCount(streamId);
  }

  private int decrementAndGetBookmarkCount(final Long streamId) {
    pollManagementRepository.decrementAndGetBookmarkCount(streamId);
    return pollManagementRepository.getBookmarkCount(streamId);
  }

  @Override
  @Transactional
  public Integer updateBookmarkCount(final Long streamId, final boolean bookmarked) {
    if (bookmarked) {
      return incrementAndGetBookmarkCount(streamId);
    } else {
      return decrementAndGetBookmarkCount(streamId);
    }
  }

  public Integer incrementLikeCount(final Long chatSpaceId) {
    pollManagementRepository.incrementAndGetLikeCount(chatSpaceId);
    return pollManagementRepository.getLikeCount(chatSpaceId);
  }

  private Integer decrementLikeCount(final Long chatSpaceId) {
    pollManagementRepository.decrementAndGetLikeCount(chatSpaceId);
    return pollManagementRepository.getLikeCount(chatSpaceId);
  }

  @Override
  @Transactional
  public Integer updateLikeCount(final Long chatSpaceId, final boolean isLiked) {
    return isLiked ? incrementLikeCount(chatSpaceId) : decrementLikeCount(chatSpaceId);
  }
}

