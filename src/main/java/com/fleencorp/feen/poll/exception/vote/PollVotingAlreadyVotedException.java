package com.fleencorp.feen.poll.exception.vote;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fleencorp.localizer.model.exception.LocalizedException;

public class PollVotingAlreadyVotedException extends LocalizedException {

  public PollVotingAlreadyVotedException(final Object...params) {
    super(params);
  }

  @Override
  @JsonIgnore
  public String getMessageCode() {
    return "poll.voting.already.voted";
  }

  public static PollVotingAlreadyVotedException of() {
    return new PollVotingAlreadyVotedException();
  }
}
