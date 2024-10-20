package com.fleencorp.feen.exception.verification;

import com.fleencorp.base.exception.FleenException;

import java.util.function.Supplier;

public class InvalidVerificationCodeException extends FleenException {

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
