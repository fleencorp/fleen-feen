package com.fleencorp.feen.exception.chat.space.core;

import com.fleencorp.localizer.model.exception.ApiException;

public class NotAnAdminOfChatSpaceException extends ApiException {

  @Override
  public String getMessageCode() {
    return "not.an.admin.of.chat.space";
  }
}
