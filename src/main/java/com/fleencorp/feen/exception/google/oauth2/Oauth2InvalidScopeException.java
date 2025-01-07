package com.fleencorp.feen.exception.google.oauth2;

import com.fleencorp.localizer.model.exception.ApiException;

public class Oauth2InvalidScopeException extends ApiException {

  @Override
  public String getMessageCode() {
    return "oauth2.invalid.scope";
  }
}
