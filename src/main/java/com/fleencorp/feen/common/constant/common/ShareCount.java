package com.fleencorp.feen.common.constant.common;

import lombok.Getter;

@Getter
public enum ShareCount {

  TOTAL_SHARE("total.share.entries");

  private final String messageCode;

  ShareCount(final String messageCode) {
    this.messageCode = messageCode;
  }

  public static ShareCount totalShares() {
    return TOTAL_SHARE;
  }
}
