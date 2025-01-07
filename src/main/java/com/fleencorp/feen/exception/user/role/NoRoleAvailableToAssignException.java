package com.fleencorp.feen.exception.user.role;

import com.fleencorp.localizer.model.exception.ApiException;

public class NoRoleAvailableToAssignException extends ApiException {

  @Override
  public String getMessageCode() {
    return "no.role.available.to.assign";
  }
}
