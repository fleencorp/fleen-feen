package com.fleencorp.feen.exception.security.mfa;

import com.fleencorp.base.exception.FleenException;

public class MfaGenerationFailedException extends FleenException {

  public static final String MESSAGE = "Error occurred during Mfa setup or operation.";

  public MfaGenerationFailedException() {
    super(MESSAGE);
  }
}
