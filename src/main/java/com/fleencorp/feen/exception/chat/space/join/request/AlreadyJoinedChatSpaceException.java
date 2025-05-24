package com.fleencorp.feen.exception.chat.space.join.request;

import com.fleencorp.localizer.model.exception.LocalizedException;

public class AlreadyJoinedChatSpaceException extends LocalizedException {

  @Override
  public String getMessageCode() {
    return "already.joined.chat.space";
  }
}
