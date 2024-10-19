package com.fleencorp.feen.exception.chat.space;

import com.fleencorp.base.exception.FleenException;

public class CannotJoinPrivateChatSpaceException extends FleenException {

  @Override
  public String getMessageCode() {
    return "cannot.join.private.chat.space";
  }

  public CannotJoinPrivateChatSpaceException(final Object...params) {
    super(params);
  }
}
