package com.fleencorp.feen.exception.verification;

import com.fleencorp.localizer.model.exception.ApiException;

public class VerificationFailedException extends ApiException {

  @Override
  public String getMessageCode() {
    return "verification.failed";
  }
}
