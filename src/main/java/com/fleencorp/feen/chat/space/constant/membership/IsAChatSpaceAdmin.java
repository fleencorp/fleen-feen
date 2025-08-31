package com.fleencorp.feen.chat.space.constant.membership;

import lombok.Getter;

@Getter
public enum IsAChatSpaceAdmin {

  NO("No", "is.a.chat.space.admin.no", "is.a.chat.space.admin.no.2"),
  YES("Yes", "is.a.chat.space.admin.yes", "is.a.chat.space.admin.yes.2");

  private final String label;
  private final String messageCode;
  private final String messageCode2;

  IsAChatSpaceAdmin(
      final String label,
      final String messageCode,
      final String messageCode2) {
    this.label = label;
    this.messageCode = messageCode;
    this.messageCode2 = messageCode2;
  }

  public static IsAChatSpaceAdmin by(final boolean isChatSpaceAdmin) {
    return isChatSpaceAdmin ? YES : NO;
  }
}
