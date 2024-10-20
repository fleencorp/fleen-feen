package com.fleencorp.feen.exception.stream;

import com.fleencorp.base.exception.FleenException;

import java.util.function.Supplier;

public class StreamAlreadyHappenedException extends FleenException {

  @Override
  public String getMessageCode() {
    return "stream.already.happened";
  }

  public StreamAlreadyHappenedException(final Object...params) {
    super(params);
  }

  public static Supplier<StreamAlreadyHappenedException> of(final Object streamId, final Object endDate) {
    return () -> new StreamAlreadyHappenedException(streamId, endDate);
  }
}
