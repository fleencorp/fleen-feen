package com.fleencorp.feen.verification.exception.core;

import com.fleencorp.localizer.model.exception.LocalizedException;

import java.util.function.Supplier;

public class ResetPasswordCodeInvalidException extends LocalizedException {

  @Override
  public String getMessageCode() {
    return "reset.password.code.invalid";
  }

  public static Supplier<ResetPasswordCodeInvalidException> of() {
    return ResetPasswordCodeInvalidException::new;
  }
}
