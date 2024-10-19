package com.fleencorp.feen.exception.social;

import com.fleencorp.base.exception.FleenException;

public class CannotProcessShareContactRequestException extends FleenException {

  private static final String MESSAGE = "Cannot process share contact request";

  public CannotProcessShareContactRequestException() {
    super(MESSAGE);
  }
}
