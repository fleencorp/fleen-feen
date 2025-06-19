package com.fleencorp.feen.verification.exception.core;

import com.fleencorp.localizer.model.exception.LocalizedException;

import java.util.function.Supplier;

public class InvalidVerificationCodeException extends LocalizedException {

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
