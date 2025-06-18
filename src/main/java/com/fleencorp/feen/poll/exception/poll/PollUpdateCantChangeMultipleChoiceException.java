package com.fleencorp.feen.poll.exception.poll;

import com.fleencorp.localizer.model.exception.LocalizedException;

public class PollUpdateCantChangeMultipleChoiceException extends LocalizedException {

  @Override
  public String getMessageCode() {
    return "poll.update.cant.change.multiple.choice";
  }

  public static PollUpdateCantChangeMultipleChoiceException of() {
    return new PollUpdateCantChangeMultipleChoiceException();
  }
}
