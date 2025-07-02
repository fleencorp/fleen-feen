package com.fleencorp.feen.review.model.holder;

import com.fleencorp.feen.model.domain.chat.ChatSpace;
import com.fleencorp.feen.model.domain.stream.FleenStream;

import java.util.Optional;

public record ReviewOtherDetailsHolder(ChatSpace chatSpace, FleenStream stream) {

  public String parentTitle() {
    return Optional.ofNullable(chatSpace)
      .map(ChatSpace::getTitle)
      .orElseGet(() -> Optional.ofNullable(stream)
        .map(FleenStream::getTitle)
        .orElse(null));
  }

  public static ReviewOtherDetailsHolder of(final ChatSpace chatSpace, final FleenStream stream) {
    return new ReviewOtherDetailsHolder(chatSpace, stream);
  }
}
