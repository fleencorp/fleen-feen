package com.fleencorp.feen.exception.user.profile;

import com.fleencorp.base.exception.FleenException;

public class DisabledAccountException extends FleenException {

  public static final String MESSAGE = "This account has been disabled.";

  public DisabledAccountException() {
    super(MESSAGE);
  }
}
