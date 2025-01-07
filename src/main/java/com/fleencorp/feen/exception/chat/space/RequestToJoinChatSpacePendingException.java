package com.fleencorp.feen.exception.chat.space;

import com.fleencorp.localizer.model.exception.ApiException;

public class RequestToJoinChatSpacePendingException extends ApiException {

  @Override
  public String getMessageCode() {
    return "request.to.join.space.pending";
  }
}
