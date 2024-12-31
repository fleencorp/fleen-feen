package com.fleencorp.feen.exception.stream;

import com.fleencorp.base.exception.FleenException;

public class StreamNotCreatedByUserException extends FleenException {

  @Override
  public String getMessageCode() {
    return "stream.stream.not.created.by.user";
  }

  public StreamNotCreatedByUserException(final Object...params) {
    super(params);
  }

  public static StreamNotCreatedByUserException of(final Object userId) {
    return new StreamNotCreatedByUserException(userId);
  }
}
