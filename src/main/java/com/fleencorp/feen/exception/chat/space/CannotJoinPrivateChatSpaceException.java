package com.fleencorp.feen.exception.chat.space;

import com.fleencorp.localizer.model.exception.ApiException;

public class CannotJoinPrivateChatSpaceException extends ApiException {

  @Override
  public String getMessageCode() {
    return "cannot.join.private.chat.space";
  }

  public CannotJoinPrivateChatSpaceException(final Object...params) {
    super(params);
  }
}
