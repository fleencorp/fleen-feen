package com.fleencorp.feen.exception.verification;

import com.fleencorp.base.exception.FleenException;

import static java.util.Objects.nonNull;

public class VerificationFailedException extends FleenException {

  public static final String MESSAGE = "Verification failed.";

  public VerificationFailedException() {
    super(MESSAGE);
  }

  public VerificationFailedException(final String message) {
    super(nonNull(message) ? message : MESSAGE);
  }
}
