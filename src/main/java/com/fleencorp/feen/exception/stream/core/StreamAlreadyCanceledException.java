package com.fleencorp.feen.exception.stream.core;

import com.fleencorp.localizer.model.exception.ApiException;

public class StreamAlreadyCanceledException extends ApiException {

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
