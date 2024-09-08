package com.fleencorp.feen.exception.auth;

import com.fleencorp.base.exception.FleenException;

public class InvalidAuthenticationTokenException extends FleenException {

  private static final String MESSAGE = "Authentication token is invalid or does not exist.";

  public InvalidAuthenticationTokenException() {
    super(MESSAGE);
  }

  public InvalidAuthenticationTokenException(final String message) {
    super(message);
  }
}
