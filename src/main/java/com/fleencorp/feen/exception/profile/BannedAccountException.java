package com.fleencorp.feen.exception.profile;

import com.fleencorp.feen.exception.base.FleenException;

public class BannedAccountException extends FleenException {

  public static final String MESSAGE = "This account has been banned.";

  public BannedAccountException() {
    super(MESSAGE);
  }

}
