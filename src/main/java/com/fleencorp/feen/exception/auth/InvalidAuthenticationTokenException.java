package com.fleencorp.feen.exception.auth;

import com.fleencorp.base.exception.FleenException;

public class InvalidAuthenticationTokenException extends FleenException {

  @Override
  public String getMessageCode() {
    return "invalid.authentication.token";
  }
}
