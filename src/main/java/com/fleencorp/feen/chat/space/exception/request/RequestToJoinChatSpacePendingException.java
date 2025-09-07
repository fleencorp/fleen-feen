package com.fleencorp.feen.chat.space.exception.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fleencorp.localizer.model.exception.LocalizedException;

public class RequestToJoinChatSpacePendingException extends LocalizedException {

  @Override
  @JsonIgnore
  public String getMessageCode() {
    return "request.to.join.space.pending";
  }
}
