package com.fleencorp.feen.poll.model.holder;

import com.fleencorp.feen.chat.space.model.domain.ChatSpace;
import com.fleencorp.feen.stream.model.domain.FleenStream;

import java.util.Optional;

public record PollParentDetailHolder(ChatSpace chatSpace, FleenStream stream) {

  public String parentTitle() {
    return Optional.ofNullable(chatSpace)
      .map(ChatSpace::getTitle)
      .orElseGet(() -> Optional.ofNullable(stream)
        .map(FleenStream::getTitle)
        .orElse(null));
  }

  public static PollParentDetailHolder of(final ChatSpace chatSpace, final FleenStream stream) {
    return new PollParentDetailHolder(chatSpace, stream);
  }

  public static PollParentDetailHolder of() {
    return new PollParentDetailHolder(null, null);
  }
}
