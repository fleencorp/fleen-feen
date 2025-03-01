package com.fleencorp.feen.exception.chat.space.join.request;

import com.fleencorp.localizer.model.exception.ApiException;

public class RequestToJoinChatSpacePendingException extends ApiException {

  @Override
  public String getMessageCode() {
    return "request.to.join.space.pending";
  }
}
