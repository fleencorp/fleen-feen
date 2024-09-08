package com.fleencorp.feen.exception.user.role;

import com.fleencorp.base.exception.FleenException;

public class NoRoleAvailableToAssignException extends FleenException {

  private static final String MESSAGE = "No role available to assign to user.";

  public NoRoleAvailableToAssignException() {
    super(MESSAGE);
  }
}
