package com.fleencorp.feen.exception.user.profile;

import com.fleencorp.localizer.model.exception.ApiException;

public class BannedAccountException extends ApiException {

  @Override
  public String getMessageCode() {
    return "banned.account";
  }
}
