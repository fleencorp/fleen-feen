package com.fleencorp.feen.softask.exception.core;

import com.fleencorp.feen.softask.constant.core.SoftAskParentType;
import com.fleencorp.localizer.model.exception.LocalizedException;

import java.util.function.Supplier;

public class SoftAskParentNotFoundException extends LocalizedException {

  private final SoftAskParentType parentType;

  public SoftAskParentNotFoundException(SoftAskParentType parentType, Object... params) {
    super(params);
    this.parentType = parentType;
  }

  @Override
  public String getMessageCode() {
    return switch (parentType) {
      case CHAT_SPACE -> "chat.space.not.found";
      case STREAM ->  "stream.not.found";
      default -> "empty";
    };
  }

  public static Supplier<SoftAskParentNotFoundException> of(final SoftAskParentType parentType, Object parentId) {
    return () -> new SoftAskParentNotFoundException(parentType, parentId);
  }
}
