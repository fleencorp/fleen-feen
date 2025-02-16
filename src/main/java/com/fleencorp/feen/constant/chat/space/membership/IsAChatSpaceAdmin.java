package com.fleencorp.feen.constant.chat.space.membership;

import com.fleencorp.base.constant.base.ApiParameter;
import lombok.Getter;

@Getter
public enum IsAChatSpaceAdmin implements ApiParameter {

  NO("No", "is.a.chat.space.admin.no", "is.a.chat.space.admin.no.2"),
  YES("Yes", "is.a.chat.space.admin.yes", "is.a.chat.space.admin.yes.2");

  private final String value;
  private final String messageCode;
  private final String messageCode2;

  IsAChatSpaceAdmin(
      final String value,
      final String messageCode,
      final String messageCode2) {
    this.value = value;
    this.messageCode = messageCode;
    this.messageCode2 = messageCode2;
  }

  public static IsAChatSpaceAdmin by(final boolean isChatSpaceAdmin) {
    return isChatSpaceAdmin ? YES : NO;
  }
}
