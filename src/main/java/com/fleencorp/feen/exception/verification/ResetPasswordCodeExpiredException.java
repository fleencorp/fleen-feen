package com.fleencorp.feen.exception.verification;

import com.fleencorp.base.exception.FleenException;

public class ResetPasswordCodeExpiredException extends FleenException {

  public static final String MESSAGE = "Reset password code is invalid or has expired.";

  public ResetPasswordCodeExpiredException() {
    super(MESSAGE);
  }

}
