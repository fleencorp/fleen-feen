package com.fleencorp.feen.mfa.exception.core;

import com.fleencorp.localizer.model.exception.LocalizedException;

public class MfaGenerationFailedException extends LocalizedException {

  @Override
  public String getMessageCode() {
    return "mfa.generation.failed";
  }
}
