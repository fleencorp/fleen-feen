package com.fleencorp.feen.user.exception.user;

import com.fleencorp.localizer.model.exception.LocalizedException;

public class UpdateProfileInfoFailedException extends LocalizedException {

  @Override
  public String getMessageCode() {
    return "update.profile.info.failed";
  }
}
