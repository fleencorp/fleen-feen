package com.fleencorp.feen.exception.chat.space;

import com.fleencorp.base.exception.FleenException;

public class AlreadyJoinedChatSpaceException extends FleenException {

  @Override
  public String getMessageCode() {
    return "already.joined.chat.space";
  }
}
