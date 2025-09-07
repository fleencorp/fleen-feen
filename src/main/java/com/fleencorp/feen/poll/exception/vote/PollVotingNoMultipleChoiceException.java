package com.fleencorp.feen.poll.exception.vote;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fleencorp.localizer.model.exception.LocalizedException;

public class PollVotingNoMultipleChoiceException extends LocalizedException {

  public PollVotingNoMultipleChoiceException(final Object...params) {
    super(params);
  }

  @Override
  @JsonIgnore
  public String getMessageCode() {
    return "poll.voting.no.multiple.choice";
  }

  public static PollVotingNoMultipleChoiceException of(final Object pollId) {
    return new PollVotingNoMultipleChoiceException(pollId);
  }
}
