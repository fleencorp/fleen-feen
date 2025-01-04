package com.fleencorp.feen.exception.verification;

import com.fleencorp.localizer.model.exception.ApiException;

public class ResetPasswordCodeExpiredException extends ApiException {

  @Override
  public String getMessageCode() {
    return "reset.password.code.expired";
  }

}
