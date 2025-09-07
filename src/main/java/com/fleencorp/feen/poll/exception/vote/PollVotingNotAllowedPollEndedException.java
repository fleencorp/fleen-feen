package com.fleencorp.feen.poll.exception.vote;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fleencorp.localizer.model.exception.LocalizedException;

public class PollVotingNotAllowedPollEndedException extends LocalizedException {

  public PollVotingNotAllowedPollEndedException(final Object...params) {
    super(params);
  }

  @Override
  @JsonIgnore
  public String getMessageCode() {
    return "poll.voting.not.allowed.poll.ended";
  }

  public static PollVotingNotAllowedPollEndedException of(final Object pollId) {
    return new PollVotingNotAllowedPollEndedException(pollId);
  }
}
