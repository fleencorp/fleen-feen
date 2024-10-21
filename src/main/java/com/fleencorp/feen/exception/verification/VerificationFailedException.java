package com.fleencorp.feen.exception.verification;

import com.fleencorp.base.exception.FleenException;

public class VerificationFailedException extends FleenException {

  @Override
  public String getMessageCode() {
    return "verification.failed";
  }
}
