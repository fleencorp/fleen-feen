package com.fleencorp.feen.exception.base;

import com.fleencorp.base.exception.FleenException;

public class FailedOperationException extends FleenException {

  private static final String MESSAGE = "Operation failed";

  public FailedOperationException() {
    super(MESSAGE);
  }
}
