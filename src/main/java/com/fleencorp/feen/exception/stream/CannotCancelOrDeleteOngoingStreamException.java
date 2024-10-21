package com.fleencorp.feen.exception.stream;

import com.fleencorp.base.exception.FleenException;

import java.util.function.Supplier;

public class CannotCancelOrDeleteOngoingStreamException extends FleenException {

  @Override
  public String getMessageCode() {
    return "cannot.cancel.or.delete.ongoing.stream";
  }

  public CannotCancelOrDeleteOngoingStreamException(final Object...params) {
    super(params);
  }

  public static Supplier<CannotCancelOrDeleteOngoingStreamException> of(final Object streamId) {
    return () -> new CannotCancelOrDeleteOngoingStreamException(streamId);
  }
}
