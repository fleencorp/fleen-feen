package com.fleencorp.feen.poll.exception.vote;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fleencorp.localizer.model.exception.LocalizedException;

public class PollVotingNotAllowedPollDeletedException extends LocalizedException {

  public PollVotingNotAllowedPollDeletedException(final Object...params) {
    super(params);
  }

  @Override
  @JsonIgnore
  public String getMessageCode() {
    return "poll.voting.not.allowed.poll.deleted";
  }

  public static PollVotingNotAllowedPollDeletedException of(final Object pollId) {
    return new PollVotingNotAllowedPollDeletedException(pollId);
  }
}
