package com.fleencorp.feen.review.model.holder;

import com.fleencorp.feen.chat.space.model.domain.ChatSpace;
import com.fleencorp.feen.stream.model.domain.FleenStream;

import java.util.Optional;

public record ReviewParentDetailHolder(ChatSpace chatSpace, FleenStream stream) {

  public String parentTitle() {
    return Optional.ofNullable(chatSpace)
      .map(ChatSpace::getTitle)
      .orElseGet(() -> Optional.ofNullable(stream)
        .map(FleenStream::getTitle)
        .orElse(null));
  }

  public static ReviewParentDetailHolder of(final ChatSpace chatSpace, final FleenStream stream) {
    return new ReviewParentDetailHolder(chatSpace, stream);
  }
}
