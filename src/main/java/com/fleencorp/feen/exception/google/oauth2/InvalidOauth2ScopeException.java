package com.fleencorp.feen.exception.google.oauth2;

import com.fleencorp.base.exception.FleenException;

public class InvalidOauth2ScopeException extends FleenException {

  private static final String MESSAGE = "Invalid Oauth2 Scope.";

  public InvalidOauth2ScopeException() {
    super(MESSAGE);
  }
}
