package com.fleencorp.feen.exception.verification;

import com.fleencorp.base.exception.FleenException;

public class ResetPasswordCodeExpiredException extends FleenException {

  @Override
  public String getMessageCode() {
    return "reset.password.code.expired";
  }

}
