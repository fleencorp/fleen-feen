package com.fleencorp.feen.chat.space.constant.membership;

import lombok.Getter;

@Getter
public enum IsChatSpaceMemberLeft {


  NO("No", "is.a.chat.space.member.left.no"),
  YES("Yes", "is.a.chat.space.member.left.yes");

  private final String label;
  private final String messageCode;

  IsChatSpaceMemberLeft(
      final String label,
      final String messageCode) {
    this.label = label;
    this.messageCode = messageCode;
  }

  public static IsChatSpaceMemberLeft by(final boolean isAChatSpaceMemberLeft) {
    return isAChatSpaceMemberLeft ? YES : NO;
  }
}
