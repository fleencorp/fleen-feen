package com.fleencorp.feen.exception.verification;

import com.fleencorp.feen.exception.base.FleenException;

public class ExpiredVerificationCodeException extends FleenException {

  public static final String MESSAGE = "Verification code has expired. Code : %s";

  public ExpiredVerificationCodeException(String code) {
    super(String.format(MESSAGE, code));
  }
}
