package com.fleencorp.feen.poll.exception.poll;

import com.fleencorp.localizer.model.exception.LocalizedException;

public class PollUpdateCantChangeVisibilityException extends LocalizedException {

  @Override
  public String getMessageCode() {
    return "poll.update.cant.change.visibility";
  }

  public static PollUpdateCantChangeVisibilityException of() {
    return new PollUpdateCantChangeVisibilityException();
  }
}
