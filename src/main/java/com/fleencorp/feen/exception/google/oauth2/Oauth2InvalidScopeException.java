package com.fleencorp.feen.exception.google.oauth2;

import com.fleencorp.base.exception.FleenException;

public class Oauth2InvalidScopeException extends FleenException {

  @Override
  public String getMessageCode() {
    return "oauth2.invalid.scope";
  }
}
