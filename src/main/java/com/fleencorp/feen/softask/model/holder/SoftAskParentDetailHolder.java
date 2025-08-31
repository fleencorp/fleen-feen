package com.fleencorp.feen.softask.model.holder;

import com.fleencorp.feen.chat.space.model.domain.ChatSpace;
import com.fleencorp.feen.model.contract.HasTitle;
import com.fleencorp.feen.softask.constant.core.SoftAskParentType;
import com.fleencorp.feen.stream.model.domain.FleenStream;

public record SoftAskParentDetailHolder(ChatSpace chatSpace, FleenStream stream, SoftAskParentType parentType) {

  public String parentTitle() {
    return switch (parentType) {
      case CHAT_SPACE -> HasTitle.getTitle(chatSpace);
      case STREAM -> HasTitle.getTitle(stream);
      default -> null;
    };
  }

  public static SoftAskParentDetailHolder of(final ChatSpace chatSpace, final FleenStream stream, final SoftAskParentType parentType) {
    return new SoftAskParentDetailHolder(chatSpace, stream, parentType);
  }

  public static SoftAskParentDetailHolder of() {
    return new SoftAskParentDetailHolder(null, null, null);
  }
}
