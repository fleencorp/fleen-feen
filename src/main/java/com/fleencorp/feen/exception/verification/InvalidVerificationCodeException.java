package com.fleencorp.feen.exception.verification;

import com.fleencorp.feen.exception.base.FleenException;

public class InvalidVerificationCodeException extends FleenException {

  public static final String MESSAGE = "Invalid verification code : %s";

  public InvalidVerificationCodeException(final String code) {
    super(String.format(MESSAGE, code));
  }
}
