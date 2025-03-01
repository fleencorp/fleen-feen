package com.fleencorp.feen.exception.chat.space.core;

import com.fleencorp.localizer.model.exception.ApiException;

public class ChatSpaceAlreadyDeletedException extends ApiException {

  @Override
  public String getMessageCode() {
    return "chat.space.already.deleted";
  }
}
