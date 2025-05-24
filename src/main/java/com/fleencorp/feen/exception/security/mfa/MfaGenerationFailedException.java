package com.fleencorp.feen.exception.security.mfa;

import com.fleencorp.localizer.model.exception.LocalizedException;

public class MfaGenerationFailedException extends LocalizedException {

  @Override
  public String getMessageCode() {
    return "mfa.generation.failed";
  }
}
