package com.fleencorp.feen.exception.auth;

import com.fleencorp.feen.exception.base.FleenException;

public class AlreadySignedUpException extends FleenException {

  public static final String MESSAGE = "This profile is already signed up and has completed the registration process.";
  private String messageKey;
  private Object[] params;

  public AlreadySignedUpException() {
    super(MESSAGE);
  }

  public AlreadySignedUpException(final String message, final Object[] params) {
    super(message);
    this.params = params;
    this.messageKey = message;
  }


}
