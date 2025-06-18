package com.fleencorp.feen.poll.constant;

import com.fleencorp.base.constant.base.ApiParameter;
import lombok.Getter;

@Getter
public enum IsMultipleChoice implements ApiParameter {

  NO("No", "is.multipleChoice.no", "is.multipleChoice.no.2"),
  YES("Yes", "is.multipleChoice.yes", "is.multipleChoice.yes.2");

  private final String value;
  private final String messageCode;
  private final String messageCode2;

  IsMultipleChoice(
      final String value,
      final String messageCode,
      final String messageCode2) {
    this.value = value;
    this.messageCode = messageCode;
    this.messageCode2 = messageCode2;
  }

  public static IsMultipleChoice by(final boolean isAnonymous) {
    return isAnonymous ? YES : NO;
  }
}
