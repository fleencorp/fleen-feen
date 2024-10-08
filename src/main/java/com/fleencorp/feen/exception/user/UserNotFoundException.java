package com.fleencorp.feen.exception.user;

import com.fleencorp.base.exception.FleenException;

import java.util.Objects;

import static com.fleencorp.feen.constant.message.ResponseMessage.UNKNOWN;

public class UserNotFoundException extends FleenException {

  public static final String MESSAGE = "User does not exist or cannot be found. ID: %s";

  public UserNotFoundException(final Object id) {
    super(String.format(MESSAGE, Objects.toString(id, UNKNOWN)));
  }
}
