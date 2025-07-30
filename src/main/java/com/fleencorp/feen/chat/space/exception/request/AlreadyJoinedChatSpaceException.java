package com.fleencorp.feen.chat.space.exception.request;

import com.fleencorp.localizer.model.exception.LocalizedException;

public class AlreadyJoinedChatSpaceException extends LocalizedException {

  @Override
  public String getMessageCode() {
    return "already.joined.chat.space";
  }
}
