package com.fleencorp.feen.exception.stream.core;

import com.fleencorp.localizer.model.exception.ApiException;

public class StreamNotCreatedByUserException extends ApiException {

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
