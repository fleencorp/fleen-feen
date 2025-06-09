package com.fleencorp.feen.user.exception.mfa;

import com.fleencorp.localizer.model.exception.LocalizedException;

public class MfaGenerationFailedException extends LocalizedException {

  @Override
  public String getMessageCode() {
    return "mfa.generation.failed";
  }
}
