package com.fleencorp.feen.exception.user.profile;

import com.fleencorp.base.exception.FleenException;

public class DisabledAccountException extends FleenException {

  @Override
  public String getMessageCode() {
    return "disabled.account";
  }
}
