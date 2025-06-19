package com.fleencorp.feen.poll.constant.core;

import com.fleencorp.base.constant.base.ApiParameter;
import lombok.Getter;

import static com.fleencorp.base.util.EnumUtil.parseEnumOrNull;

@Getter
public enum PollParentType implements ApiParameter {

  CHAT_SPACE("Chat Space"),
  NONE("None"),
  STREAM("Stream"),;

  private final String value;

  PollParentType(final String value) {
    this.value = value;
  }

  public static PollParentType of(final String value) {
    return parseEnumOrNull(value, PollParentType.class);
  }

  public static boolean isChatSpace(final PollParentType pollParentType) {
    return pollParentType == CHAT_SPACE;
  }

  public static boolean isNone(final PollParentType pollParentType) {
    return pollParentType == NONE;
  }

  public static boolean isStream(final PollParentType pollParentType) {
    return pollParentType == STREAM;
  }

}
