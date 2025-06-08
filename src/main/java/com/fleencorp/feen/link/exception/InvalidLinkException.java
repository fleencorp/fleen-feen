package com.fleencorp.feen.link.exception;

import com.fleencorp.localizer.model.exception.LocalizedException;

public class InvalidLinkException extends LocalizedException {

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
