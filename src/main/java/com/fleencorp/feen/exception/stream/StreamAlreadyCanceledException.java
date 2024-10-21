package com.fleencorp.feen.exception.stream;

import com.fleencorp.base.exception.FleenException;

public class StreamAlreadyCanceledException extends FleenException {

  @Override
  public String getMessageCode() {
    return "stream.already.canceled";
  }

  public StreamAlreadyCanceledException(final Object...params) {
    super(params);
  }

  public static FleenStreamNotCreatedByUserException of(final Object streamId) {
    return new FleenStreamNotCreatedByUserException(streamId);
  }
}
