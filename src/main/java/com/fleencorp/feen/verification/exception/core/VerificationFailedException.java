package com.fleencorp.feen.verification.exception.core;

import com.fleencorp.localizer.model.exception.LocalizedException;

public class VerificationFailedException extends LocalizedException {

  @Override
  public String getMessageCode() {
    return "verification.failed";
  }
}
