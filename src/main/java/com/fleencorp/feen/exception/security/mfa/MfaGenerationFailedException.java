package com.fleencorp.feen.exception.security.mfa;

import com.fleencorp.base.exception.FleenException;

public class MfaGenerationFailedException extends FleenException {

  @Override
  public String getMessageCode() {
    return "mfa.generation.failed";
  }
}
