package com.fleencorp.feen.poll.constant;

import com.fleencorp.base.constant.base.ApiParameter;
import lombok.Getter;

@Getter
public enum IsEnded implements ApiParameter {

  NO("No", "is.ended.no", "is.ended.no.2"),
  YES("Yes", "is.ended.yes", "is.ended.yes.2");

  private final String value;
  private final String messageCode;
  private final String messageCode2;

  IsEnded(
      final String value,
      final String messageCode,
      final String messageCode2) {
    this.value = value;
    this.messageCode = messageCode;
    this.messageCode2 = messageCode2;
  }

  public static IsEnded by(final boolean ended) {
    return ended ? YES : NO;
  }
}
