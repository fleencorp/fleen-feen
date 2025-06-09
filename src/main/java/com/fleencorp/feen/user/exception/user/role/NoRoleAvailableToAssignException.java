package com.fleencorp.feen.user.exception.user.role;

import com.fleencorp.localizer.model.exception.LocalizedException;

public class NoRoleAvailableToAssignException extends LocalizedException {

  @Override
  public String getMessageCode() {
    return "no.role.available.to.assign";
  }
}
