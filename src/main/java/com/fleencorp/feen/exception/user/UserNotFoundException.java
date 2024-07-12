package com.fleencorp.feen.exception.user;

import com.fleencorp.feen.exception.base.FleenException;

import java.util.Objects;

import static com.fleencorp.feen.constant.message.ResponseMessage.UNKNOWN;

public class UserNotFoundException extends FleenException {

  public static final String MESSAGE = "User does not exist or cannot be found. ID: %s";

  public UserNotFoundException(Object id) {
    super(String.format(MESSAGE, Objects.toString(id, UNKNOWN)));
  }
}
