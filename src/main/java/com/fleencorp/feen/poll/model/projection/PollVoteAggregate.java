package com.fleencorp.feen.poll.model.projection;

public interface PollVoteAggregate {
  Long getOptionId();
  String getOptionText();
  Long getVoteCount();
  Long getTotalVotes();

}


