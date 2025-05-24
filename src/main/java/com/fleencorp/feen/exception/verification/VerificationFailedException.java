package com.fleencorp.feen.exception.verification;

import com.fleencorp.localizer.model.exception.LocalizedException;

public class VerificationFailedException extends LocalizedException {

  @Override
  public String getMessageCode() {
    return "verification.failed";
  }
}
