package com.fleencorp.feen.exception.share;

import com.fleencorp.base.exception.FleenException;

public class CannotCancelShareContactRequestException extends FleenException {

  private static final String MESSAGE = "Cannot cancel share contact request";

  public CannotCancelShareContactRequestException() {
    super(MESSAGE);
  }
}
