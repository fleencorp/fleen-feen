package com.fleencorp.feen.exception.verification;

import com.fleencorp.base.exception.FleenException;

public class ExpiredVerificationCodeException extends FleenException {

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
