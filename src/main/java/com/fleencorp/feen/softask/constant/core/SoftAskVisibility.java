package com.fleencorp.feen.softask.constant.core;

import com.fleencorp.base.constant.base.ApiParameter;
import lombok.Getter;

import static com.fleencorp.base.util.EnumUtil.parseEnumOrNull;

@Getter
public enum SoftAskVisibility implements ApiParameter {

  PUBLIC("Public"),
  PRIVATE("Private");

  private final String value;

  SoftAskVisibility(final String value) {
    this.value = value;
  }

  public static SoftAskVisibility of(final String value) {
    return parseEnumOrNull(value, SoftAskVisibility.class);
  }
}
