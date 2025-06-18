package com.fleencorp.feen.poll.model.holder;

import com.fleencorp.feen.poll.model.projection.PollVoteAggregate;

import java.util.List;

public record PollVoteAggregateHolder(List<PollVoteAggregate> pollVotesAggregates) {

  public boolean hasVotes() {
    return !pollVotesAggregates.isEmpty() && pollVotesAggregates.getFirst().totalVotes() > 0;
  }

  public Integer totalVotes() {
    return (int) (pollVotesAggregates.isEmpty() ? 0L : pollVotesAggregates.getFirst().totalVotes());
  }

  public static PollVoteAggregateHolder of(final List<PollVoteAggregate> pollVotesAggregates) {
    return new PollVoteAggregateHolder(pollVotesAggregates);
  }
}
