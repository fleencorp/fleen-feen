package com.fleencorp.feen.exception.stream;

import com.fleencorp.localizer.model.exception.ApiException;

import java.util.function.Supplier;

public class StreamNotFoundException extends ApiException {

  @Override
  public String getMessageCode() {
    return "stream.not.found";
  }

  public StreamNotFoundException(final Object...params) {
    super(params);
  }

  public static Supplier<StreamNotFoundException> of(final Object streamId) {
    return () -> new StreamNotFoundException(streamId);
  }
}
