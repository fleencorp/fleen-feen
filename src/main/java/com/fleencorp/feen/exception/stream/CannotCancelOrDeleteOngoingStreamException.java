package com.fleencorp.feen.exception.stream;

import com.fleencorp.localizer.model.exception.ApiException;

import java.util.function.Supplier;

public class CannotCancelOrDeleteOngoingStreamException extends ApiException {

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
