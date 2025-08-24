package com.fleencorp.feen.softask.constant.core;

import com.fleencorp.base.constant.base.ApiParameter;
import lombok.Getter;

import static com.fleencorp.base.util.EnumUtil.parseEnumOrNull;

@Getter
public enum SoftAskType implements ApiParameter {

  SOFT_ASK("Soft Ask"),
  SOFT_ASK_REPLY("Reply");

  private final String value;

  SoftAskType(final String value) {
    this.value = value;
  }

  public static SoftAskType of(final String value) {
    return parseEnumOrNull(value, SoftAskType.class);
  }

  public static boolean isSoftAsk(final SoftAskType softAskType) {
    return SOFT_ASK == softAskType;
  }

  public static boolean isReply(final SoftAskType softAskType) {
    return SOFT_ASK_REPLY == softAskType;
  }
}
