package com.fleencorp.feen.chat.space.exception.core;

import com.fleencorp.localizer.model.exception.LocalizedException;

import java.util.function.Supplier;

public class ChatSpaceNotFoundException extends LocalizedException {

  public ChatSpaceNotFoundException(final Object...params) {
    super(params);
  }

  @Override
  public String getMessageCode() {
    return "chat.space.not.found";
  }

  public static Supplier<ChatSpaceNotFoundException> of(final Object chatSpaceId) {
    return () -> new ChatSpaceNotFoundException(chatSpaceId);
  }
}
