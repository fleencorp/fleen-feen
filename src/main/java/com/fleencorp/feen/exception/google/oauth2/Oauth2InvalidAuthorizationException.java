package com.fleencorp.feen.exception.google.oauth2;

import com.fleencorp.feen.exception.base.FleenException;

public class Oauth2InvalidAuthorizationException extends FleenException {

  public static final String MESSAGE = "Oauth2, Invalid Authorization or incomplete authentication";

  public Oauth2InvalidAuthorizationException() {
    super(MESSAGE);
  }
}
