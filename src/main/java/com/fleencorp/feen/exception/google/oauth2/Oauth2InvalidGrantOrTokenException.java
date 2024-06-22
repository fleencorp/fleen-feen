package com.fleencorp.feen.exception.google.oauth2;

import com.fleencorp.feen.exception.base.FleenException;

import static java.lang.String.format;

public class Oauth2InvalidGrantOrTokenException extends FleenException {

  public static final String MESSAGE = "Oauth2, Invalid Grant or Token. Authorization Code: %s";

  public Oauth2InvalidGrantOrTokenException(String authorizationCode) {
    super(format(MESSAGE, authorizationCode));
  }
}
