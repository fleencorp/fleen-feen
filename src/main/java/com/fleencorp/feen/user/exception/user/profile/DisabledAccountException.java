package com.fleencorp.feen.user.exception.user.profile;

import com.fleencorp.localizer.model.exception.LocalizedException;

public class DisabledAccountException extends LocalizedException {

  @Override
  public String getMessageCode() {
    return "disabled.account";
  }
}
