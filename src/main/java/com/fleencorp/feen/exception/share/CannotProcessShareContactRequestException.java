package com.fleencorp.feen.exception.share;

import com.fleencorp.feen.exception.base.FleenException;

public class CannotProcessShareContactRequestException extends FleenException {

  private static final String MESSAGE = "Cannot process share contact request";

  public CannotProcessShareContactRequestException() {
    super(MESSAGE);
  }
}
