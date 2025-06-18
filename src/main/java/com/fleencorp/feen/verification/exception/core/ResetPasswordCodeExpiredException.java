package com.fleencorp.feen.verification.exception.core;

import com.fleencorp.localizer.model.exception.LocalizedException;

public class ResetPasswordCodeExpiredException extends LocalizedException {

  @Override
  public String getMessageCode() {
    return "reset.password.code.expired";
  }

}
