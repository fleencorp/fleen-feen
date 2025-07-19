package com.fleencorp.feen.softask.exception.core;

import com.fleencorp.localizer.model.exception.LocalizedException;

import java.util.function.Supplier;

public class SoftAskAnswerNotFoundException extends LocalizedException {

  public SoftAskAnswerNotFoundException(final Object...params) {
    super(params);
  }

  @Override
  public String getMessageCode() {
    return "soft.ask.answer.not.found";
  }

  public static Supplier<SoftAskAnswerNotFoundException> of(final Object answerId) {
    return () -> new SoftAskAnswerNotFoundException(answerId);
  }
}
