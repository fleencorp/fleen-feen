package com.fleencorp.feen.mfa.exception.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fleencorp.localizer.model.exception.LocalizedException;

public class MfaGenerationFailedException extends LocalizedException {

  @Override
  @JsonIgnore
  public String getMessageCode() {
    return "mfa.generation.failed";
  }
}
