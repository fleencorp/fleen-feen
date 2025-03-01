package com.fleencorp.feen.exception.chat.space.join.request;

import com.fleencorp.localizer.model.exception.ApiException;

public class AlreadyJoinedChatSpaceException extends ApiException {

  @Override
  public String getMessageCode() {
    return "already.joined.chat.space";
  }
}
