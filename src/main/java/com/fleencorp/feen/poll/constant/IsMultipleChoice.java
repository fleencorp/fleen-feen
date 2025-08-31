package com.fleencorp.feen.poll.constant;

import lombok.Getter;

@Getter
public enum IsMultipleChoice {

  NO("No", "is.multipleChoice.no", "is.multipleChoice.no.2"),
  YES("Yes", "is.multipleChoice.yes", "is.multipleChoice.yes.2");

  private final String label;
  private final String messageCode;
  private final String messageCode2;

  IsMultipleChoice(
      final String label,
      final String messageCode,
      final String messageCode2) {
    this.label = label;
    this.messageCode = messageCode;
    this.messageCode2 = messageCode2;
  }

  public static IsMultipleChoice by(final boolean isAnonymous) {
    return isAnonymous ? YES : NO;
  }
}
