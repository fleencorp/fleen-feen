package com.fleencorp.feen.exception.user.profile;

import com.fleencorp.base.exception.FleenException;

public class BannedAccountException extends FleenException {

  @Override
  public String getMessageCode() {
    return "banned.account";
  }
}
