package com.fleencorp.feen.exception.user.role;

import com.fleencorp.base.exception.FleenException;

public class NoRoleAvailableToAssignException extends FleenException {

  @Override
  public String getMessageCode() {
    return "no.role.available.to.assign";
  }
}
