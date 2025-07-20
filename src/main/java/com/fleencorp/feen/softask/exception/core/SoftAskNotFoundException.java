package com.fleencorp.feen.softask.exception.core;

import com.fleencorp.localizer.model.exception.LocalizedException;

import java.util.function.Supplier;

public class SoftAskNotFoundException extends LocalizedException {

  public SoftAskNotFoundException(final Object...params) {
    super(params);
  }

  @Override
  public String getMessageCode() {
    return "soft.ask.not.found";
  }

  public static Supplier<SoftAskNotFoundException> of(final Object softAskId) {
    return () -> new SoftAskNotFoundException(softAskId);
  }
}
