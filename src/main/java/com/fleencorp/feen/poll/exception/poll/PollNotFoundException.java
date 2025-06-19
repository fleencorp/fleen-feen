package com.fleencorp.feen.poll.exception.poll;

import com.fleencorp.localizer.model.exception.LocalizedException;

import java.util.function.Supplier;

public class PollNotFoundException extends LocalizedException {

  protected PollNotFoundException(final Object...params) {
    super(params);
  }

  @Override
  public String getMessageCode() {
    return "poll.not.found";
  }

  public static Supplier<PollNotFoundException> of(final Object id) {
    return () -> new PollNotFoundException(id);
  }
}
