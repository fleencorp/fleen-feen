package com.fleencorp.feen.chat.space.constant.membership;

import lombok.Getter;

@Getter
public enum IsAChatSpaceMember {

  NO("No", "is.a.chat.space.member.no"),
  YES("Yes", "is.a.chat.space.member.yes");

  private final String label;
  private final String messageCode;

  IsAChatSpaceMember(
      final String label,
      final String messageCode) {
    this.label = label;
    this.messageCode = messageCode;
  }

  public static IsAChatSpaceMember by(final boolean isAChatSpaceMember) {
    return isAChatSpaceMember ? YES : NO;
  }
}
