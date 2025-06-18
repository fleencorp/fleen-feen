package com.fleencorp.feen.oauth2.exception.core;

import com.fleencorp.localizer.model.exception.LocalizedException;

public class Oauth2InvalidScopeException extends LocalizedException {

  @Override
  public String getMessageCode() {
    return "oauth2.invalid.scope";
  }
}
