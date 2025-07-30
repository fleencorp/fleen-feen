package com.fleencorp.feen.stream.exception.core;

import com.fleencorp.localizer.model.exception.LocalizedException;

public class StreamNotCreatedByUserException extends LocalizedException {

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
