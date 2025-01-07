package com.fleencorp.feen.exception.verification;

import com.fleencorp.localizer.model.exception.ApiException;

import java.util.function.Supplier;

public class InvalidVerificationCodeException extends ApiException {

  @Override
  public String getMessageCode() {
    return "invalid.verification.code";
  }

  public InvalidVerificationCodeException(final Object...params) {
    super(params);
  }

  public static Supplier<InvalidVerificationCodeException> of(final Object verificationCode) {
    return () -> new InvalidVerificationCodeException(verificationCode);
  }
}
