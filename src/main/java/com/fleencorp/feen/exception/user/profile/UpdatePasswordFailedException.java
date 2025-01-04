package com.fleencorp.feen.exception.user.profile;

import com.fleencorp.localizer.model.exception.ApiException;

public class UpdatePasswordFailedException extends ApiException {

  @Override
  public String getMessageCode() {
    return "update.password.failed";
  }
}
