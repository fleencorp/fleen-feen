package com.fleencorp.feen.chat.space.exception.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fleencorp.localizer.model.exception.LocalizedException;

public class ChatSpaceNotActiveException extends LocalizedException {

  @Override
  @JsonIgnore
  public String getMessageCode() {
    return "chat.space.not.active";
  }
}
