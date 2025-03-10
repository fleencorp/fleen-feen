package com.fleencorp.feen.model.holder;

import com.fleencorp.feen.model.domain.stream.FleenStream;

public record ReviewOtherDetailsHolder(FleenStream stream) {

  public static ReviewOtherDetailsHolder of(final FleenStream stream) {
    return new ReviewOtherDetailsHolder(stream);
  }
}
