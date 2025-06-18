package com.fleencorp.feen.poll.exception.option;

import com.fleencorp.localizer.model.exception.LocalizedException;

public class PollUpdateCantChangeOptionsException extends LocalizedException {

  @Override
  public String getMessageCode() {
    return "poll.update.cant.change.options";
  }

  public static PollUpdateCantChangeOptionsException of() {
    return new PollUpdateCantChangeOptionsException();
  }
}
