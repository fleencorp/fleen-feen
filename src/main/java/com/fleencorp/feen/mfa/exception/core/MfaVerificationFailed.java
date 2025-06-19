package com.fleencorp.feen.mfa.exception.core;

import com.fleencorp.localizer.model.exception.LocalizedException;

public class MfaVerificationFailed extends LocalizedException {

  @Override
  public String getMessageCode() {
    return "mfa.verification.failed";
  }
}
