package com.fleencorp.feen.poll.model.holder;

import com.fleencorp.feen.chat.space.model.domain.ChatSpace;
import com.fleencorp.feen.model.contract.HasTitle;
import com.fleencorp.feen.poll.constant.core.PollParentType;
import com.fleencorp.feen.stream.model.domain.FleenStream;

public record PollParentDetailHolder(ChatSpace chatSpace, FleenStream stream, PollParentType parentType) {

  public String parentTitle() {
    return switch (parentType) {
      case CHAT_SPACE -> HasTitle.getTitle(chatSpace);
      case STREAM -> HasTitle.getTitle(stream);
      case NONE -> null;
    };
  }

  public static PollParentDetailHolder of(final ChatSpace chatSpace, final FleenStream stream, final PollParentType parentType) {
    return new PollParentDetailHolder(chatSpace, stream, parentType);
  }
}
