package com.fleencorp.feen.user.exception.authentication;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fleencorp.localizer.model.exception.LocalizedException;

public class InvalidAuthenticationTokenException extends LocalizedException {

  @Override
  @JsonIgnore
  public String getMessageCode() {
    return "invalid.authentication.token";
  }
}
