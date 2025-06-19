package com.fleencorp.feen.poll.constant;

import com.fleencorp.base.constant.base.ApiParameter;
import lombok.Getter;

@Getter
public enum IsVoted implements ApiParameter {

  NO("No", "is.voted.no", "is.voted.no.2"),
  YES("Yes", "is.voted.yes", "is.voted.yes.2");

  private final String value;
  private final String messageCode;
  private final String messageCode2;

  IsVoted(
      final String value,
      final String messageCode,
      final String messageCode2) {
    this.value = value;
    this.messageCode = messageCode;
    this.messageCode2 = messageCode2;
  }

  public static IsVoted by(final boolean isVoted) {
    return isVoted ? YES : NO;
  }
}
