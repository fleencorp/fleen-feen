package com.fleencorp.feen.softask.model.holder;

import com.fleencorp.feen.model.contract.HasTitle;
import com.fleencorp.feen.shared.chat.space.contract.IsAChatSpace;
import com.fleencorp.feen.shared.stream.contract.IsAStream;
import com.fleencorp.feen.softask.constant.core.SoftAskParentType;

public record SoftAskParentDetailHolder(IsAChatSpace chatSpace, IsAStream stream, SoftAskParentType parentType) {

  public String parentTitle() {
    return switch (parentType) {
      case CHAT_SPACE -> HasTitle.getTitle(chatSpace);
      case STREAM -> HasTitle.getTitle(stream);
      default -> null;
    };
  }

  public static SoftAskParentDetailHolder of(final IsAChatSpace chatSpace, final IsAStream stream, final SoftAskParentType parentType) {
    return new SoftAskParentDetailHolder(chatSpace, stream, parentType);
  }

  public static SoftAskParentDetailHolder empty() {
    return new SoftAskParentDetailHolder(null, null, null);
  }
}
