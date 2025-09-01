package com.fleencorp.feen.poll.constant;

import lombok.Getter;

@Getter
public enum IsPollMultipleChoice {

  NO("No", "is.poll.multipleChoice.no", "is.poll.multipleChoice.no.2"),
  YES("Yes", "is.poll.multipleChoice.yes", "is.poll.multipleChoice.yes.2");

  private final String label;
  private final String messageCode;
  private final String messageCode2;

  IsPollMultipleChoice(
      final String label,
      final String messageCode,
      final String messageCode2) {
    this.label = label;
    this.messageCode = messageCode;
    this.messageCode2 = messageCode2;
  }

  public static IsPollMultipleChoice by(final boolean isAnonymous) {
    return isAnonymous ? YES : NO;
  }
}
