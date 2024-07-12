package com.fleencorp.feen.exception.verification;

import com.fleencorp.feen.exception.base.FleenException;

import static java.util.Objects.nonNull;

public class VerificationFailedException extends FleenException {

  public static final String MESSAGE = "Verification failed.";

  public VerificationFailedException() {
    super(MESSAGE);
  }

  public VerificationFailedException(String message) {
    super(nonNull(message) ? message : MESSAGE);
  }
}
