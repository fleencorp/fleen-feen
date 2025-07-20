package com.fleencorp.feen.chat.space.exception.request;

import com.fleencorp.localizer.model.exception.LocalizedException;

public class RequestToJoinChatSpacePendingException extends LocalizedException {

  @Override
  public String getMessageCode() {
    return "request.to.join.space.pending";
  }
}
