package com.fleencorp.feen.chat.space.constant.membership;

import lombok.Getter;

@Getter
public enum IsChatSpaceMemberRemoved {

  NO("No", "is.a.chat.space.member.removed.no"),
  YES("Yes", "is.a.chat.space.member.removed.yes");

  private final String value;
  private final String messageCode;

  IsChatSpaceMemberRemoved(
      final String value,
      final String messageCode) {
    this.value = value;
    this.messageCode = messageCode;
  }

  public static IsChatSpaceMemberRemoved by(final boolean isAChatSpaceMemberRemoved) {
    return isAChatSpaceMemberRemoved ? YES : NO;
  }
}
