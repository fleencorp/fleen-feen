package com.fleencorp.feen.verification.exception.core;

import com.fleencorp.localizer.model.exception.LocalizedException;

public class ExpiredVerificationCodeException extends LocalizedException {

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
