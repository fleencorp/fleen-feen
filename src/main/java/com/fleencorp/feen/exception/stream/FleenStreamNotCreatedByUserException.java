package com.fleencorp.feen.exception.stream;

import com.fleencorp.base.exception.FleenException;

public class FleenStreamNotCreatedByUserException extends FleenException {

  @Override
  public String getMessageCode() {
    return "fleen.stream.stream.not.created.by.user";
  }

  public FleenStreamNotCreatedByUserException(final Object...params) {
    super(params);
  }

  public static FleenStreamNotCreatedByUserException of(final Object userId) {
    return new FleenStreamNotCreatedByUserException(userId);
  }
}
