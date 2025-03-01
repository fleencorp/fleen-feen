package com.fleencorp.feen.exception.base;

import com.fleencorp.localizer.model.exception.ApiException;

public class FailedOperationException extends ApiException {

  @Override
  public String getMessageCode() {
    return "failed.operation";
  }

  public static FailedOperationException of() {
    return new FailedOperationException();
  }
}
