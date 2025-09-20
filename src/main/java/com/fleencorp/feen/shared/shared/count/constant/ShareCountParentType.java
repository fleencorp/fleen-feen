package com.fleencorp.feen.shared.shared.count.constant;

import static com.fleencorp.base.util.EnumUtil.parseEnumOrNull;

public enum ShareCountParentType {

  BUSINESS,
  CHAT_SPACE,
  JOB_OPPORTUNITY,
  POLL,
  REVIEW,
  SOFT_ASK,
  SOFT_ASK_REPLY,
  STREAM;

  public static ShareCountParentType of(final String value) {
    return parseEnumOrNull(value, ShareCountParentType.class);
  }
}
