package com.fleencorp.feen.exception.base;

import com.fleencorp.base.exception.FleenException;

public class FailedOperationException extends FleenException {

  @Override
  public String getMessageCode() {
    return "failed.operation";
  }
}
