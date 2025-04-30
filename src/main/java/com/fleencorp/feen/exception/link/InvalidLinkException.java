package com.fleencorp.feen.exception.link;

import com.fleencorp.localizer.model.exception.ApiException;

public class InvalidLinkException extends ApiException {

  @Override
  public String getMessageCode() {
    return "invalid.link";
  }

  public InvalidLinkException(final Object...params) {
    super(params);
  }

  public static InvalidLinkException of() {
    return new InvalidLinkException();
  }
}
