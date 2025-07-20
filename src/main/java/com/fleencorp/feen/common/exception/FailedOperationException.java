package com.fleencorp.feen.common.exception;

import com.fleencorp.localizer.model.exception.LocalizedException;

public class FailedOperationException extends LocalizedException {

  @Override
  public String getMessageCode() {
    return "failed.operation";
  }

  public static FailedOperationException of() {
    return new FailedOperationException();
  }
}
