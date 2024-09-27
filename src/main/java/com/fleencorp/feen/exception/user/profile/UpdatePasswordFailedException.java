package com.fleencorp.feen.exception.user.profile;

import com.fleencorp.base.exception.FleenException;

public class UpdatePasswordFailedException extends FleenException {

  @Override
  public String getMessageCode() {
    return "update.password.failed";
  }

  public UpdatePasswordFailedException() {
    super();
  }
}
