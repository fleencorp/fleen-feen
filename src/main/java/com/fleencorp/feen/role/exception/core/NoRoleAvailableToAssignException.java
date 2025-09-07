package com.fleencorp.feen.role.exception.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fleencorp.localizer.model.exception.LocalizedException;

public class NoRoleAvailableToAssignException extends LocalizedException {

  @Override
  @JsonIgnore
  public String getMessageCode() {
    return "no.role.available.to.assign";
  }
}
