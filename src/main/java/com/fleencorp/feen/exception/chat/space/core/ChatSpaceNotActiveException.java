package com.fleencorp.feen.exception.chat.space.core;

import com.fleencorp.localizer.model.exception.LocalizedException;

public class ChatSpaceNotActiveException extends LocalizedException {

  @Override
  public String getMessageCode() {
    return "chat.space.not.active";
  }
}
