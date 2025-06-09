package com.fleencorp.feen.user.exception.verification;

import com.fleencorp.localizer.model.exception.LocalizedException;

public class ResetPasswordCodeExpiredException extends LocalizedException {

  @Override
  public String getMessageCode() {
    return "reset.password.code.expired";
  }

}
