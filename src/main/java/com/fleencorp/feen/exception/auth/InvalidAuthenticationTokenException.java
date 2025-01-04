package com.fleencorp.feen.exception.auth;

import com.fleencorp.localizer.model.exception.ApiException;

public class InvalidAuthenticationTokenException extends ApiException {

  @Override
  public String getMessageCode() {
    return "invalid.authentication.token";
  }
}
