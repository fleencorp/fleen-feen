package com.fleencorp.feen.softask.constant.core;

import com.fleencorp.base.constant.base.ApiParameter;
import lombok.Getter;

import static com.fleencorp.base.util.EnumUtil.parseEnumOrNull;

@Getter
public enum SoftAskParentType implements ApiParameter {

  CHAT_SPACE("Chat Space"),
  POLL("Poll"),
  STREAM("Stream");

  private final String value;

  SoftAskParentType(final String value) {
    this.value = value;
  }

  public static SoftAskParentType of(final String value) {
    return parseEnumOrNull(value, SoftAskParentType.class);
  }

  public static boolean isChatSpace(final SoftAskParentType softAskParentType) {
    return softAskParentType == CHAT_SPACE;
  }

  public static boolean isPoll(final SoftAskParentType softAskParentType) {
    return softAskParentType == POLL;
  }

  public static boolean isStream(final SoftAskParentType softAskParentType) {
    return softAskParentType == STREAM;
  }
}
