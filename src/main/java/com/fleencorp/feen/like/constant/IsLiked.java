package com.fleencorp.feen.like.constant;

import com.fleencorp.base.constant.base.ApiParameter;
import lombok.Getter;

@Getter
public enum IsLiked implements ApiParameter {

  NO("No", "is.liked.no", "is.liked.no.2"),
  YES("Yes", "is.liked.yes", "is.liked.yes.2"),;

  private final String value;
  private final String messageCode;
  private final String messageCode2;

  IsLiked(
      final String value,
      final String messageCode,
      final String messageCode2) {
    this.value = value;
    this.messageCode = messageCode;
    this.messageCode2 = messageCode2;
  }

  public static IsLiked by(final boolean liked) {
    return liked ? YES : NO;
  }
}
