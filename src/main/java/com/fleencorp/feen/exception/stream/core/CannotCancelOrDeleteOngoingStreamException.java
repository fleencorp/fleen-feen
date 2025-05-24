package com.fleencorp.feen.exception.stream.core;

import com.fleencorp.localizer.model.exception.LocalizedException;

public class CannotCancelOrDeleteOngoingStreamException extends LocalizedException {

  @Override
  public String getMessageCode() {
    return "cannot.cancel.or.delete.ongoing.stream";
  }

  public CannotCancelOrDeleteOngoingStreamException(final Object...params) {
    super(params);
  }

  public static CannotCancelOrDeleteOngoingStreamException of(final Object streamId) {
    return new CannotCancelOrDeleteOngoingStreamException(streamId);
  }
}
