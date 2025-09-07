package com.fleencorp.feen.link.exception.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fleencorp.localizer.model.exception.LocalizedException;

public class InvalidLinkException extends LocalizedException {

  @Override
  @JsonIgnore
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
