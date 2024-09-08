package com.fleencorp.feen.exception.verification;

import com.fleencorp.base.exception.FleenException;

public class ResetPasswordCodeInvalidException extends FleenException {

  public static final String MESSAGE = "Reset password code is invalid or has expired.";

  public ResetPasswordCodeInvalidException() {
    super(MESSAGE);
  }
}
