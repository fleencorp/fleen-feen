package com.fleencorp.feen.model.holder;

import com.fleencorp.feen.model.domain.chat.ChatSpace;
import com.fleencorp.feen.model.domain.stream.FleenStream;

public record LikeOtherDetailsHolder(FleenStream stream, ChatSpace chatSpace) {

  public static LikeOtherDetailsHolder of(final FleenStream stream, final ChatSpace chatSpace) {
    return new LikeOtherDetailsHolder(stream, chatSpace);
  }
}
