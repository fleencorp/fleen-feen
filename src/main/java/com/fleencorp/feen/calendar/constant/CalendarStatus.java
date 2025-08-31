package com.fleencorp.feen.calendar.constant;

import lombok.Getter;

import static com.fleencorp.base.util.EnumUtil.parseEnumOrNull;

@Getter
public enum CalendarStatus {
  
  ACTIVE("Active", "calendar.active", "calendar.active.2"),
  INACTIVE("Inactive", "calendar.inactive", "calendar.inactive.2");

  private final String label;
  private final String messageCode;
  private final String messageCode2;

  CalendarStatus(
      final String label,
      final String messageCode,
      final String messageCode2) {
    this.label = label;
    this.messageCode = messageCode;
    this.messageCode2 = messageCode2;
  }

  public static CalendarStatus of(final String value) {
    return parseEnumOrNull(value, CalendarStatus.class);
  }

  public static boolean isActive(final CalendarStatus status) {
    return status == ACTIVE;
  }
}
