package com.fleencorp.feen.poll.model.holder;

import com.fleencorp.feen.poll.model.domain.PollVote;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

public record PollVoteEntriesHolder(List<PollVote> votes) {

  public Map<Long, List<PollVote>> groupPollVotesById(final List<PollVote> votes) {
    return votes.stream()
      .filter(pollVote -> nonNull(pollVote.getPoll()))
      .collect(Collectors.groupingBy(vote -> vote.getPoll().getPollId()));
  }

  public List<PollVote> getPollVotes(final Long pollId) {
    final Map<Long, List<PollVote>> groupVotes = groupPollVotesById(votes);
    return groupVotes.getOrDefault(pollId, List.of());
  }

  public Collection<Long> getPollVoteIds() {
    return votes.stream()
      .map(PollVote::getPollOptionId)
      .collect(Collectors.toList());
  }

  public boolean hasVotes() {
    return nonNull(votes) && !votes.isEmpty();
  }

  public static PollVoteEntriesHolder of(final List<PollVote> pollVotes) {
    return new PollVoteEntriesHolder(pollVotes);
  }
}
