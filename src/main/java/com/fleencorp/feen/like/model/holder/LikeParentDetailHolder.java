package com.fleencorp.feen.like.model.holder;

import com.fleencorp.feen.chat.space.model.domain.ChatSpace;
import com.fleencorp.feen.stream.model.domain.FleenStream;

import java.util.Optional;

public record LikeParentDetailHolder(FleenStream stream, ChatSpace chatSpace) {

  public String parentTitle() {
    return Optional.ofNullable(chatSpace)
      .map(ChatSpace::getTitle)
      .or(() -> Optional.ofNullable(stream).map(FleenStream::getTitle))
      .orElse(null);
  }

  public static LikeParentDetailHolder of(final FleenStream stream, final ChatSpace chatSpace) {
    return new LikeParentDetailHolder(stream, chatSpace);
  }
}
