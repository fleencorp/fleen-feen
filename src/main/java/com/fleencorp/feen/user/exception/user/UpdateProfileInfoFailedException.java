package com.fleencorp.feen.user.exception.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fleencorp.localizer.model.exception.LocalizedException;

public class UpdateProfileInfoFailedException extends LocalizedException {

  @Override
  @JsonIgnore
  public String getMessageCode() {
    return "update.profile.info.failed";
  }
}
