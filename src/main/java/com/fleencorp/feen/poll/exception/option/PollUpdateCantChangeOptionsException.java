package com.fleencorp.feen.poll.exception.option;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fleencorp.localizer.model.exception.LocalizedException;

public class PollUpdateCantChangeOptionsException extends LocalizedException {

  @Override
  @JsonIgnore
  public String getMessageCode() {
    return "poll.update.cant.change.options";
  }

  public static PollUpdateCantChangeOptionsException of() {
    return new PollUpdateCantChangeOptionsException();
  }
}
