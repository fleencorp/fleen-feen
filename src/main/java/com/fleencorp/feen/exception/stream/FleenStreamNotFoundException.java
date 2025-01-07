package com.fleencorp.feen.exception.stream;

import com.fleencorp.localizer.model.exception.ApiException;

import java.util.function.Supplier;

public class FleenStreamNotFoundException extends ApiException {

  @Override
  public String getMessageCode() {
    return "stream.not.found";
  }

  public FleenStreamNotFoundException(final Object...params) {
    super(params);
  }

  public static Supplier<FleenStreamNotFoundException> of(final Object streamId) {
    return () -> new FleenStreamNotFoundException(streamId);
  }
}
