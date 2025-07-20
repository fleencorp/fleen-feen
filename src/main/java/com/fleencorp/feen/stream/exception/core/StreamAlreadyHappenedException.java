package com.fleencorp.feen.stream.exception.core;

import com.fleencorp.localizer.model.exception.LocalizedException;

import java.util.function.Supplier;

public class StreamAlreadyHappenedException extends LocalizedException {

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
