package com.fleencorp.feen.user.exception.authentication;

import com.fleencorp.localizer.model.exception.LocalizedException;

public class InvalidAuthenticationTokenException extends LocalizedException {

  @Override
  public String getMessageCode() {
    return "invalid.authentication.token";
  }
}
