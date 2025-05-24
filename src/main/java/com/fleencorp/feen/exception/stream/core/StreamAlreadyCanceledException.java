package com.fleencorp.feen.exception.stream.core;

import com.fleencorp.localizer.model.exception.LocalizedException;

public class StreamAlreadyCanceledException extends LocalizedException {

  @Override
  public String getMessageCode() {
    return "stream.already.canceled";
  }

  public StreamAlreadyCanceledException(final Object...params) {
    super(params);
  }

  public static StreamNotCreatedByUserException of(final Object streamId) {
    return new StreamNotCreatedByUserException(streamId);
  }
}
