package com.fleencorp.feen.exception.base;

import com.fleencorp.localizer.model.exception.LocalizedException;

public class UnableToCompleteOperationException extends LocalizedException {

  @Override
  public String getMessageCode() {
    return "unable.to.complete.operation";
  }
}
