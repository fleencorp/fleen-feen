package com.fleencorp.feen.business.constant;

import lombok.Getter;

import static com.fleencorp.base.util.EnumUtil.parseEnumOrNull;

@Getter
public enum BusinessStatus {

  ACTIVE("Active", "business.status.active", "business.status.active.2"),
  INACTIVE("Inactive", "business.status.inactive", "business.status.inactive.2");

  private final String label;
  private final String messageCode;
  private final String messageCode2;

  BusinessStatus(
      final String label,
      final String messageCode,
      final String messageCode2) {
    this.label = label;
    this.messageCode = messageCode;
    this.messageCode2 = messageCode2;
  }

  public static BusinessStatus of(final String value) {
    return parseEnumOrNull(value, BusinessStatus.class);
  }
}
