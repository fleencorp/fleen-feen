package com.fleencorp.feen.common.constant.social;

import lombok.Getter;

import static com.fleencorp.base.util.EnumUtil.parseEnumOrNull;

@Getter
public enum BlockStatus {

  BLOCKED("Blocked"),
  UNBLOCKED("Unblocked");

  private final String label;

  BlockStatus(final String label) {
    this.label = label;
  }

  public static BlockStatus of(final String value) {
    return parseEnumOrNull(value, BlockStatus.class);
  }

  public static boolean isBlocked(final BlockStatus blockStatus) {
    return blockStatus == BLOCKED;
  }
}
