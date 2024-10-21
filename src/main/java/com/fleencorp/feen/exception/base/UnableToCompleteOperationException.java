package com.fleencorp.feen.exception.base;

import com.fleencorp.base.exception.FleenException;

public class UnableToCompleteOperationException extends FleenException {

  @Override
  public String getMessageCode() {
    return "unable.to.complete.operation";
  }
}
