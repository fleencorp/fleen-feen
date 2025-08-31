package com.fleencorp.feen.business.exception;

import com.fleencorp.localizer.model.exception.LocalizedException;

public class BusinessNotOwnerException extends LocalizedException {

  public BusinessNotOwnerException() {
    super();
  }

  @Override
  public String getMessageCode() {
    return "business.not.owner";
  }

  public static BusinessNotOwnerException of() {
    return new BusinessNotOwnerException();
  }
}
