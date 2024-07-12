package com.fleencorp.feen.exception.auth;

import com.fleencorp.feen.exception.base.FleenException;

public class AlreadySignedUpException extends FleenException {

  public static final String MESSAGE = "This profile is already signed up and has completed the registration process.";

  public AlreadySignedUpException() {
    super(MESSAGE);
  }
}
