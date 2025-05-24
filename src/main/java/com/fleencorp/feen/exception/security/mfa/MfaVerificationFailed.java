package com.fleencorp.feen.exception.security.mfa;

import com.fleencorp.localizer.model.exception.LocalizedException;

public class MfaVerificationFailed extends LocalizedException {

  @Override
  public String getMessageCode() {
    return "mfa.verification.failed";
  }
}
