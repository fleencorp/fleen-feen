package com.fleencorp.feen.like.constant;

import lombok.Getter;

@Getter
public enum IsLiked {

  NO("No", "is.liked.no", "is.liked.no.2"),
  YES("Yes", "is.liked.yes", "is.liked.yes.2"),;

  private final String label;
  private final String messageCode;
  private final String messageCode2;

  IsLiked(
      final String label,
      final String messageCode,
      final String messageCode2) {
    this.label = label;
    this.messageCode = messageCode;
    this.messageCode2 = messageCode2;
  }

  public static IsLiked by(final boolean liked) {
    return liked ? YES : NO;
  }
}
