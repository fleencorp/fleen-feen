package com.fleencorp.feen.role.exception.core;

import com.fleencorp.localizer.model.exception.LocalizedException;

public class NoRoleAvailableToAssignException extends LocalizedException {

  @Override
  public String getMessageCode() {
    return "no.role.available.to.assign";
  }
}
