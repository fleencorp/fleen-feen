package com.fleencorp.feen.chat.space.exception.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fleencorp.localizer.model.exception.LocalizedException;

public class AlreadyJoinedChatSpaceException extends LocalizedException {

  @Override
  @JsonIgnore
  public String getMessageCode() {
    return "already.joined.chat.space";
  }
}
