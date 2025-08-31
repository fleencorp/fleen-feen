package com.fleencorp.feen.common.constant.stat;

import lombok.Getter;

@Getter
public enum TotalFollowed {

  TOTAL_FOLLOWED("Total Followed", "total.followed", "total.followed.2");

  private final String label;
  private final String messageCode;
  private final String messageCode2;

  TotalFollowed(
    final String label,
    final String messageCode,
    final String messageCode2) {
    this.label = label;
    this.messageCode = messageCode;
    this.messageCode2 = messageCode2;
  }
}
