package com.fleencorp.feen.softask.exception.core;

import com.fleencorp.localizer.model.exception.LocalizedException;

import java.util.function.Supplier;

public class SoftAskReplyNotFoundException extends LocalizedException {

  public SoftAskReplyNotFoundException(final Object...params) {
    super(params);
  }

  @Override
  public String getMessageCode() {
    return "soft.ask.reply.not.found";
  }

  public static Supplier<SoftAskReplyNotFoundException> of(final Object replyId) {
    return () -> new SoftAskReplyNotFoundException(replyId);
  }
}
