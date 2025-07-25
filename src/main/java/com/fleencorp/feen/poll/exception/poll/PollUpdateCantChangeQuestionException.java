package com.fleencorp.feen.poll.exception.poll;

import com.fleencorp.localizer.model.exception.LocalizedException;

public class PollUpdateCantChangeQuestionException extends LocalizedException {

  @Override
  public String getMessageCode() {
    return "poll.update.cant.change.question";
  }

  public static PollUpdateCantChangeQuestionException of() {
    return new PollUpdateCantChangeQuestionException();
  }
}
