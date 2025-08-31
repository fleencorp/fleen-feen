package com.fleencorp.feen.common.constant.common;

import lombok.Getter;

@Getter
public enum IsDeleted {

  NO("No", "is.deleted.no", "is.deleted.no.2"),
  YES("Yes", "is.deleted.yes", "is.deleted.yes.2");

  private final String label;
  private final String messageCode;
  private final String messageCode2;

  IsDeleted(
      final String label,
      final String messageCode,
      final String messageCode2) {
    this.label = label;
    this.messageCode = messageCode;
    this.messageCode2 = messageCode2;
  }

  public static IsDeleted by(final boolean isDeleted) {
    return isDeleted ? YES : NO;
  }
}
