package com.fleencorp.feen.exception.chat.space;

import com.fleencorp.base.exception.FleenException;

public class ChatSpaceAlreadyDeletedException extends FleenException {

  @Override
  public String getMessageCode() {
    return "chat.space.already.deleted";
  }
}
