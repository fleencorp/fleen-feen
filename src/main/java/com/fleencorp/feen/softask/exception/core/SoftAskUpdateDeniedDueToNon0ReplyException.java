package com.fleencorp.feen.softask.exception.core;

import com.fleencorp.localizer.model.exception.LocalizedException;

public class SoftAskUpdateDeniedDueToNon0ReplyException extends LocalizedException {

  public SoftAskUpdateDeniedDueToNon0ReplyException(final Object...params) {
    super(params);
  }

  @Override
  public String getMessageCode() {
    return "soft.ask.update.denied.due.to.non.zero.reply";
  }

  public static SoftAskUpdateDeniedDueToNon0ReplyException of() {
    return new SoftAskUpdateDeniedDueToNon0ReplyException();
  }
}
