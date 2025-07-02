package com.fleencorp.feen.poll.model.holder;

import com.fleencorp.feen.model.domain.chat.ChatSpace;
import com.fleencorp.feen.model.domain.stream.FleenStream;

import java.util.Optional;

public record AddPollParentDetailHolder(ChatSpace chatSpace, FleenStream stream) {

  public String parentTitle() {
    return Optional.ofNullable(chatSpace)
      .map(ChatSpace::getTitle)
      .orElseGet(() -> Optional.ofNullable(stream)
        .map(FleenStream::getTitle)
        .orElse(null));
  }


  public static AddPollParentDetailHolder of(final ChatSpace chatSpace, final FleenStream stream) {
    return new AddPollParentDetailHolder(chatSpace, stream);
  }

  public static AddPollParentDetailHolder of() {
    return new AddPollParentDetailHolder(null, null);
  }
}
