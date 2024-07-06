package com.fleencorp.feen.exception.stream;

import com.fleencorp.feen.exception.base.FleenException;

import java.util.Objects;

import static com.fleencorp.feen.constant.message.ResponseMessage.UNKNOWN;
import static java.lang.String.format;

public class FleenStreamNotCreatedByUserException extends FleenException {

  private static final String MESSAGE = "Stream or event not created by user. ID: %s";

  public FleenStreamNotCreatedByUserException(final Object userId) {
    super(format(FleenStreamNotCreatedByUserException.MESSAGE, Objects.toString(userId, UNKNOWN)));
  }
}
