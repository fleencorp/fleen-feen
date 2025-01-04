package com.fleencorp.feen.exception.security.mfa;

import com.fleencorp.localizer.model.exception.ApiException;

public class MfaGenerationFailedException extends ApiException {

  @Override
  public String getMessageCode() {
    return "mfa.generation.failed";
  }
}
