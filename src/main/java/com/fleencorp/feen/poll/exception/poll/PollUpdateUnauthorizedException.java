package com.fleencorp.feen.poll.exception.poll;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fleencorp.localizer.model.exception.LocalizedException;

public class PollUpdateUnauthorizedException extends LocalizedException {

  public PollUpdateUnauthorizedException(final Object...params) {
    super(params);
  }

  @Override
  @JsonIgnore
  public String getMessageCode() {
    return "poll.update.unauthorized";
  }

  public static PollUpdateUnauthorizedException of(final Object pollId) {
    return new PollUpdateUnauthorizedException(pollId);
  }
}
