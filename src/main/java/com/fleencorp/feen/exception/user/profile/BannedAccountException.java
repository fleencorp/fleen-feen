package com.fleencorp.feen.exception.user.profile;

import com.fleencorp.base.exception.FleenException;

public class BannedAccountException extends FleenException {

  public static final String MESSAGE = "This account has been banned.";

  public BannedAccountException() {
    super(MESSAGE);
  }

}
