package com.fleencorp.feen.exception.user.profile;

import com.fleencorp.localizer.model.exception.ApiException;

public class DisabledAccountException extends ApiException {

  @Override
  public String getMessageCode() {
    return "disabled.account";
  }
}
