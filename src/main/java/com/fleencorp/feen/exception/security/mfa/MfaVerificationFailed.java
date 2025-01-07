package com.fleencorp.feen.exception.security.mfa;

import com.fleencorp.localizer.model.exception.ApiException;

public class MfaVerificationFailed extends ApiException {

  @Override
  public String getMessageCode() {
    return "mfa.verification.failed";
  }
}
