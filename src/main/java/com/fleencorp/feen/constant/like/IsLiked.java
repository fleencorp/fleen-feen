package com.fleencorp.feen.constant.like;

import com.fleencorp.base.constant.base.ApiParameter;
import lombok.Getter;

@Getter
public enum IsLiked implements ApiParameter {

  NO("No", "is.liked.no.otherText"),
  YES("Yes", "is.liked.yes.otherText");

  private final String value;
  private final String messageCode;

  IsLiked(
      final String value,
      final String messageCode) {
    this.value = value;
    this.messageCode = messageCode;
  }

  public static IsLiked by(final boolean liked) {
    return liked ? YES : NO;
  }
}
