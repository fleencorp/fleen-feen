package com.fleencorp.feen.exception.user.profile;

import com.fleencorp.localizer.model.exception.ApiException;

public class UpdateProfileInfoFailedException extends ApiException {

  @Override
  public String getMessageCode() {
    return "update.profile.info.failed";
  }
}
