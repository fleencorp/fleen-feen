package com.fleencorp.feen.exception.stream.core;

import com.fleencorp.localizer.model.exception.LocalizedException;

public class CannotUpdateOngoingStreamException extends LocalizedException {

  @Override
  public String getMessageCode() {
    return "cannot.update.ongoing.stream";
  }

  public CannotUpdateOngoingStreamException(final Object...params) {
    super(params);
  }

  public static CannotUpdateOngoingStreamException of(final Object streamId) {
    return new CannotUpdateOngoingStreamException(streamId);
  }
}
