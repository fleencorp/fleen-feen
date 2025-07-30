package com.fleencorp.feen.chat.space.constant.membership;

import com.fleencorp.base.constant.base.ApiParameter;
import lombok.Getter;

@Getter
public enum IsChatSpaceMemberLeft implements ApiParameter {


  NO("No", "is.a.chat.space.member.left.no"),
  YES("Yes", "is.a.chat.space.member.left.yes");

  private final String value;
  private final String messageCode;

  IsChatSpaceMemberLeft(
      final String value,
      final String messageCode) {
    this.value = value;
    this.messageCode = messageCode;
  }

  public static IsChatSpaceMemberLeft by(final boolean isAChatSpaceMemberLeft) {
    return isAChatSpaceMemberLeft ? YES : NO;
  }
}
