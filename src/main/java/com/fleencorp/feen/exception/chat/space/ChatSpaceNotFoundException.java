package com.fleencorp.feen.exception.chat.space;

import com.fleencorp.base.exception.FleenException;

public class ChatSpaceNotFoundException extends FleenException {

  @Override
  public String getMessageCode() {
    return "chat.space.not.found";
  }

  public ChatSpaceNotFoundException(final Object...params) {
    super(params);
  }
}
