package com.fleencorp.feen.exception.stream;

import com.fleencorp.feen.exception.base.FleenException;

public class UnableToCompleteOperationException extends FleenException {

  private static final String MESSAGE = "Unable to complete operation.";

  public UnableToCompleteOperationException() {
    super(UnableToCompleteOperationException.MESSAGE);
  }
}
