package com.fleencorp.feen.poll.constant;

import lombok.Getter;

@Getter
public enum PollIsEnded {

  NO("No", "is.poll.ended.no", "is.poll.ended.no.2"),
  YES("Yes", "is.poll.ended.yes", "is.poll.ended.yes.2");

  private final String label;
  private final String messageCode;
  private final String messageCode2;

  PollIsEnded(
      final String label,
      final String messageCode,
      final String messageCode2) {
    this.label = label;
    this.messageCode = messageCode;
    this.messageCode2 = messageCode2;
  }

  public static PollIsEnded by(final boolean ended) {
    return ended ? YES : NO;
  }
}
