package com.fleencorp.feen.exception.verification;

import com.fleencorp.localizer.model.exception.ApiException;

import java.util.function.Supplier;

public class ResetPasswordCodeInvalidException extends ApiException {

  @Override
  public String getMessageCode() {
    return "reset.password.code.invalid";
  }

  public static Supplier<ResetPasswordCodeInvalidException> of() {
    return ResetPasswordCodeInvalidException::new;
  }
}
