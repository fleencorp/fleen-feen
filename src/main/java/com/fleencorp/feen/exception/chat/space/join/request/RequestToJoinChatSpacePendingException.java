package com.fleencorp.feen.exception.chat.space.join.request;

import com.fleencorp.localizer.model.exception.LocalizedException;

public class RequestToJoinChatSpacePendingException extends LocalizedException {

  @Override
  public String getMessageCode() {
    return "request.to.join.space.pending";
  }
}
