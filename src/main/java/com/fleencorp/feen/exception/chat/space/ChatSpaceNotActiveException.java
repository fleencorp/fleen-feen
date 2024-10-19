package com.fleencorp.feen.exception.chat.space;

import com.fleencorp.base.exception.FleenException;

public class ChatSpaceNotActiveException extends FleenException {

  @Override
  public String getMessageCode() {
    return "chat.space.not.active";
  }
}
