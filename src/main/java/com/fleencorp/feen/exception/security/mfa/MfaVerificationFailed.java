package com.fleencorp.feen.exception.security.mfa;

import com.fleencorp.base.exception.FleenException;

public class MfaVerificationFailed extends FleenException {

  public static final String MESSAGE = "An error occurred while completing the (MFA) setup.";

  public MfaVerificationFailed() {
    super(MESSAGE);
  }
}
