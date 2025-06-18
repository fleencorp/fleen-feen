package com.fleencorp.feen.mfa.constant;

import lombok.Getter;

@Getter
public enum IsMfaEnabled {

  NO("No", "is.mfa.enabled.no"),
  YES("Yes", "is.mfa.enabled.yes"),;

  private final String value;
  private final String messageCode;

  IsMfaEnabled(
      final String value,
      final String messageCode) {
    this.value = value;
    this.messageCode = messageCode;
  }

  public static IsMfaEnabled by(final boolean isMfaEnabled) {
    return isMfaEnabled ? YES : NO;
  }
}
