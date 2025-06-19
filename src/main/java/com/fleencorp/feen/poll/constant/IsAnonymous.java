package com.fleencorp.feen.poll.constant;

import com.fleencorp.base.constant.base.ApiParameter;
import lombok.Getter;

@Getter
public enum IsAnonymous implements ApiParameter {

  NO("No", "is.anonymous.no", "is.anonymous.no.2"),
  YES("Yes", "is.anonymous.yes", "is.anonymous.yes.2");

  private final String value;
  private final String messageCode;
  private final String messageCode2;

  IsAnonymous(
      final String value,
      final String messageCode,
      final String messageCode2) {
    this.value = value;
    this.messageCode = messageCode;
    this.messageCode2 = messageCode2;
  }

  public static IsAnonymous by(final boolean isAnonymous) {
    return isAnonymous ? YES : NO;
  }
}
