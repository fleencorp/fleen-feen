package com.fleencorp.feen.exception.chat.space;

import com.fleencorp.localizer.model.exception.ApiException;

import java.util.function.Supplier;

public class ChatSpaceNotFoundException extends ApiException {

  @Override
  public String getMessageCode() {
    return "chat.space.not.found";
  }

  public ChatSpaceNotFoundException(final Object...params) {
    super(params);
  }

  public static Supplier<ChatSpaceNotFoundException> of(final Object calendarId) {
    return () -> new ChatSpaceNotFoundException(calendarId);
  }
}
