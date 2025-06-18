package com.fleencorp.feen.poll.exception.vote;

import com.fleencorp.localizer.model.exception.LocalizedException;

public class PollVotingNotAllowedPollNoOptionException extends LocalizedException {

  public PollVotingNotAllowedPollNoOptionException(final Object...params) {
    super(params);
  }

  @Override
  public String getMessageCode() {
    return "poll.voting.not.allowed.poll.no.option";
  }

  public static PollVotingNotAllowedPollNoOptionException of(final Object pollId) {
    return new PollVotingNotAllowedPollNoOptionException(pollId);
  }
}
