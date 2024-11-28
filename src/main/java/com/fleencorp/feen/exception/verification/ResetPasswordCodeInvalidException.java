package com.fleencorp.feen.exception.verification;

import com.fleencorp.base.exception.FleenException;

import java.util.function.Supplier;

public class ResetPasswordCodeInvalidException extends FleenException {

  @Override
  public String getMessageCode() {
    return "reset.password.code.invalid";
  }

  public static Supplier<ResetPasswordCodeInvalidException> of() {
    return ResetPasswordCodeInvalidException::new;
  }
}
