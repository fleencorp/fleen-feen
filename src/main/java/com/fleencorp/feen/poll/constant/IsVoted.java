package com.fleencorp.feen.poll.constant;

import lombok.Getter;

@Getter
public enum IsVoted {

  NO("No", "is.voted.no", "is.voted.no.2", "is.voted.no.3", "is.voted.no.4"),
  YES("Yes", "is.voted.yes", "is.voted.yes.2", "is.voted.yes.3", "is.voted.yes.4");

  private final String label;
  private final String messageCode;
  private final String messageCode2;
  private final String messageCode3;
  private final String messageCode4;

  IsVoted(
      final String label,
      final String messageCode,
      final String messageCode2,
      final String messageCode3,
      final String messageCode4) {
    this.label = label;
    this.messageCode = messageCode;
    this.messageCode2 = messageCode2;
    this.messageCode3 = messageCode3;
    this.messageCode4 = messageCode4;
  }

  public static IsVoted by(final boolean isVoted) {
    return isVoted ? YES : NO;
  }
}
