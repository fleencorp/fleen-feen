package com.fleencorp.feen.poll.exception.poll;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fleencorp.localizer.model.exception.LocalizedException;

public class PollUpdateCantChangeAnonymityException extends LocalizedException {

  public PollUpdateCantChangeAnonymityException(final Object...args) {
    super(args);
  }

  @Override
  @JsonIgnore
  public String getMessageCode() {
    return "poll.update.cant.change.anonymity";
  }

  public static PollUpdateCantChangeAnonymityException of(final Object...args) {
    return new PollUpdateCantChangeAnonymityException(args);
  }
}
