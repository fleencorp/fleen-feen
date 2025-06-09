package com.fleencorp.feen.user.exception.auth;

import com.fleencorp.localizer.model.exception.LocalizedException;

public class InvalidAuthenticationTokenException extends LocalizedException {

  @Override
  public String getMessageCode() {
    return "invalid.authentication.token";
  }
}
