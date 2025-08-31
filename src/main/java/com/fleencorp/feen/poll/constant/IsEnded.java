package com.fleencorp.feen.poll.constant;

import lombok.Getter;

@Getter
public enum IsEnded {

  NO("No", "is.ended.no", "is.ended.no.2"),
  YES("Yes", "is.ended.yes", "is.ended.yes.2");

  private final String label;
  private final String messageCode;
  private final String messageCode2;

  IsEnded(
      final String label,
      final String messageCode,
      final String messageCode2) {
    this.label = label;
    this.messageCode = messageCode;
    this.messageCode2 = messageCode2;
  }

  public static IsEnded by(final boolean ended) {
    return ended ? YES : NO;
  }
}
