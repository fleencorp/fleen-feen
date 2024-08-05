package com.fleencorp.feen.exception.auth;

import com.fleencorp.feen.exception.base.FleenException;

import java.util.Objects;

import static com.fleencorp.feen.constant.message.ResponseMessage.UNKNOWN;

public class InvalidAuthenticationException extends FleenException {

  public static final String MESSAGE = "The username or password credential is invalid. ID: %s";

  public InvalidAuthenticationException(final String emailAddress) {
    super(String.format(MESSAGE, Objects.toString(emailAddress, UNKNOWN)));
  }
}
