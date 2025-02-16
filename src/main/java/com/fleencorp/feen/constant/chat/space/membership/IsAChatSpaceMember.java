package com.fleencorp.feen.constant.chat.space.membership;

import com.fleencorp.base.constant.base.ApiParameter;
import lombok.Getter;

@Getter
public enum IsAChatSpaceMember implements ApiParameter {

  NO("No", "is.a.chat.space.member.no"),
  YES("Yes", "is.a.chat.space.member.yes");

  private final String value;
  private final String messageCode;

  IsAChatSpaceMember(
      final String value,
      final String messageCode) {
    this.value = value;
    this.messageCode = messageCode;
  }

  public static IsAChatSpaceMember by(final boolean isAChatSpaceMember) {
    return isAChatSpaceMember ? YES : NO;
  }
}
