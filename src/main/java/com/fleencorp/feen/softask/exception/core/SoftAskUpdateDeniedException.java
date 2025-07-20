package com.fleencorp.feen.softask.exception.core;

import com.fleencorp.localizer.model.exception.LocalizedException;

public class SoftAskUpdateDeniedException extends LocalizedException {

  public SoftAskUpdateDeniedException(final Object...params) {
    super(params);
  }

  @Override
  public String getMessageCode() {
    return "soft.ask.update.denied";
  }

  public static SoftAskUpdateDeniedException of() {
    return new SoftAskUpdateDeniedException();
  }
}
