package com.fleencorp.feen.exception.chat.space.core;

import com.fleencorp.localizer.model.exception.LocalizedException;

public class NotAnAdminOfChatSpaceException extends LocalizedException {

  @Override
  public String getMessageCode() {
    return "not.an.admin.of.chat.space";
  }
}
