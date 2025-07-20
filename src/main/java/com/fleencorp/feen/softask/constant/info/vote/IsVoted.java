package com.fleencorp.feen.softask.constant.info.vote;

import com.fleencorp.base.constant.base.ApiParameter;
import lombok.Getter;

@Getter
public enum IsVoted implements ApiParameter {

  NO("No", "is.voted.no.otherText"),
  YES("Yes", "is.voted.yes.otherText");

  private final String value;
  private final String messageCode;

  IsVoted(
      final String value,
      final String messageCode) {
    this.value = value;
    this.messageCode = messageCode;
  }

  public static IsVoted by(final boolean voted) {
    return voted ? YES : NO;
  }
}
