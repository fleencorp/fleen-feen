package com.fleencorp.feen.exception.base;

public class FailedOperationException extends FleenException {

  private static final String MESSAGE = "Operation failed";

  public FailedOperationException() {
    super(MESSAGE);
  }
}
