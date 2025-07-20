package com.fleencorp.feen.softask.constant.core;

import com.fleencorp.base.constant.base.ApiParameter;
import lombok.Getter;

import static com.fleencorp.base.util.EnumUtil.parseEnumOrNull;

@Getter
public enum SoftAskType implements ApiParameter {

  ANSWER("Answer"),
  REPLY("Reply"),
  SOFT_ASK("Soft Ask");

  private final String value;

  SoftAskType(final String value) {
    this.value = value;
  }

  public static SoftAskType of(final String value) {
    return parseEnumOrNull(value, SoftAskType.class);
  }
}
