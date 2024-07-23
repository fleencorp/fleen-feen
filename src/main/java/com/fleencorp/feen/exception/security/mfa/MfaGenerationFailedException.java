package com.fleencorp.feen.exception.security.mfa;

import com.fleencorp.feen.exception.base.FleenException;

public class MfaGenerationFailedException extends FleenException {

  public static final String MESSAGE = "Error occurred during Mfa setup or operation.";

  public MfaGenerationFailedException() {
    super(MESSAGE);
  }
}
