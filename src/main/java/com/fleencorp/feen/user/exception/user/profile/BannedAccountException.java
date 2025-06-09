package com.fleencorp.feen.user.exception.user.profile;

import com.fleencorp.localizer.model.exception.LocalizedException;

public class BannedAccountException extends LocalizedException {

  @Override
  public String getMessageCode() {
    return "banned.account";
  }
}
