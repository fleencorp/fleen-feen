package com.fleencorp.feen.softask.exception.core;

import com.fleencorp.feen.softask.constant.core.SoftAskParentType;
import com.fleencorp.localizer.model.exception.LocalizedException;

import java.util.function.Supplier;

import static java.util.Objects.nonNull;

public class SoftAskParentNotFoundException extends LocalizedException {

  private final SoftAskParentType parentType;

  public SoftAskParentNotFoundException(SoftAskParentType parentType, Object... params) {
    super(params);
    this.parentType = parentType;
  }

  @Override
  public String getMessageCode() {
    if (nonNull(parentType)) {
      return switch (parentType) {
        case CHAT_SPACE -> "chat.space.not.found";
        case POLL -> "poll.not.found";
        case STREAM ->  "stream.not.found";
      };
    }

    return "empty";
  }

  public static Supplier<SoftAskParentNotFoundException> of(final SoftAskParentType parentType, Long parentId) {
    return () -> new SoftAskParentNotFoundException(parentType, parentId);
  }
}
