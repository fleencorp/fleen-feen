package com.fleencorp.feen.exception.role;

import com.fleencorp.feen.exception.base.FleenException;

public class NoRoleAvailableToAssignException extends FleenException {

  private static final String MESSAGE = "No role available to assign to user.";

  public NoRoleAvailableToAssignException() {
    super(MESSAGE);
  }
}
