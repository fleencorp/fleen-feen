package com.fleencorp.feen.exception.chat.space;

import com.fleencorp.localizer.model.exception.ApiException;

public class ChatSpaceNotActiveException extends ApiException {

  @Override
  public String getMessageCode() {
    return "chat.space.not.active";
  }
}
