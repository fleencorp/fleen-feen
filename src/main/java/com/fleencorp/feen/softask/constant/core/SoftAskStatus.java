package com.fleencorp.feen.softask.constant.core;

import com.fleencorp.base.constant.base.ApiParameter;
import lombok.Getter;

import static com.fleencorp.base.util.EnumUtil.parseEnumOrNull;

@Getter
public enum SoftAskStatus implements ApiParameter {

  ANONYMOUS("Anonymous"),
  NON_ANONYMOUS("Non Anonymous");

  private final String value;

  SoftAskStatus(final String value) {
    this.value = value;
  }

  public static SoftAskStatus of(final String value) {
    return parseEnumOrNull(value, SoftAskStatus.class);
  }
}
