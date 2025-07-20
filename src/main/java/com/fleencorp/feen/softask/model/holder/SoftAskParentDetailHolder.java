package com.fleencorp.feen.softask.model.holder;

import com.fleencorp.feen.chat.space.model.domain.ChatSpace;
import com.fleencorp.feen.stream.model.domain.FleenStream;
import com.fleencorp.feen.softask.constant.core.SoftAskParentType;

import java.util.Optional;

import static java.util.Objects.nonNull;

public record SoftAskParentDetailHolder(ChatSpace chatSpace, FleenStream stream) {

  public String parentTitle() {
    return Optional.ofNullable(chatSpace)
      .map(ChatSpace::getTitle)
      .orElseGet(() -> Optional.ofNullable(stream)
        .map(FleenStream::getTitle)
        .orElse(null));
  }

  public SoftAskParentType parentType() {
    if (nonNull(chatSpace)) {
      return SoftAskParentType.CHAT_SPACE;
    } else if (nonNull(stream)) {
      return SoftAskParentType.STREAM;
    }
    return null;
  }


  public static SoftAskParentDetailHolder of(final ChatSpace chatSpace, final FleenStream stream) {
    return new SoftAskParentDetailHolder(chatSpace, stream);
  }

  public static SoftAskParentDetailHolder of() {
    return new SoftAskParentDetailHolder(null, null);
  }
}
