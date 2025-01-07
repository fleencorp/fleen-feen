package com.fleencorp.feen.exception.base;

import com.fleencorp.localizer.model.exception.ApiException;

public class UnableToCompleteOperationException extends ApiException {

  @Override
  public String getMessageCode() {
    return "unable.to.complete.operation";
  }
}
