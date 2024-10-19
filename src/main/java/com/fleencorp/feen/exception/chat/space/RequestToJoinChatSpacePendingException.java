package com.fleencorp.feen.exception.chat.space;

import com.fleencorp.base.exception.FleenException;

public class RequestToJoinChatSpacePendingException extends FleenException {

  @Override
  public String getMessageCode() {
    return "request.to.join.space.pending";
  }
}
