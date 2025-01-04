package com.fleencorp.feen.exception.verification;

import com.fleencorp.localizer.model.exception.ApiException;

public class ExpiredVerificationCodeException extends ApiException {

  @Override
  public String getMessageCode() {
    return "expired.verification.code";
  }

  public ExpiredVerificationCodeException(final Object...params) {
    super(params);
  }

  public static ExpiredVerificationCodeException of(final Object code) {
    return new ExpiredVerificationCodeException(code);
  }
}
