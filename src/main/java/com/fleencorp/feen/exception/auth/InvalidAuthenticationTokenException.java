package com.fleencorp.feen.exception.auth;

import com.fleencorp.feen.exception.base.FleenException;

public class InvalidAuthenticationTokenException extends FleenException {

  private static final String MESSAGE = "Authentication token is invalid or does not exist.";

  public InvalidAuthenticationTokenException() {
    super(MESSAGE);
  }

  public InvalidAuthenticationTokenException(String message) {
    super(message);
  }
}
