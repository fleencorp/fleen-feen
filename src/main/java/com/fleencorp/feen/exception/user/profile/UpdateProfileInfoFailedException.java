package com.fleencorp.feen.exception.user.profile;

import com.fleencorp.base.exception.FleenException;

public class UpdateProfileInfoFailedException extends FleenException {

  @Override
  public String getMessageCode() {
    return "update.profile.info.failed";
  }
}
