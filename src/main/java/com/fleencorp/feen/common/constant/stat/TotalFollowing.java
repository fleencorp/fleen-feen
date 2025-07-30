package com.fleencorp.feen.common.constant.stat;

import com.fleencorp.base.constant.base.ApiParameter;
import lombok.Getter;

@Getter
public enum TotalFollowing implements ApiParameter {

  TOTAL_FOLLOWING("Total Following", "total.following", "total.following.2");

  private final String value;
  private final String messageCode;
  private final String messageCode2;

  TotalFollowing(
    final String value,
    final String messageCode,
    final String messageCode2) {
    this.value = value;
    this.messageCode = messageCode;
    this.messageCode2 = messageCode2;
  }
}
