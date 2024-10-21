package com.fleencorp.feen.exception.chat.space;

import com.fleencorp.base.exception.FleenException;

public class NotAnAdminOfChatSpaceException extends FleenException {

  @Override
  public String getMessageCode() {
    return "not.an.admin.of.chat.space";
  }
}
