package com.fleencorp.feen.poll.constant;

import com.fleencorp.base.constant.base.ApiParameter;
import lombok.Getter;

@Getter
public enum IsPollAnonymous implements ApiParameter {

  NO("No", "is.poll.anonymous.no", "is.poll.anonymous.no.2"),
  YES("Yes", "is.poll.anonymous.yes", "is.poll.anonymous.yes.2");

  private final String value;
  private final String messageCode;
  private final String messageCode2;

  IsPollAnonymous(
      final String value,
      final String messageCode,
      final String messageCode2) {
    this.value = value;
    this.messageCode = messageCode;
    this.messageCode2 = messageCode2;
  }

  public static IsPollAnonymous by(final boolean isAnonymous) {
    return isAnonymous ? YES : NO;
  }
}
