package com.fleencorp.feen.stream.constant.core;

import lombok.Getter;

import static com.fleencorp.base.util.EnumUtil.parseEnumOrNull;

@Getter
public enum StreamCreationType {

  INSTANT("Instant", "stream.creation.type.instant"),
  SCHEDULED("Scheduled", "stream.creation.type.scheduled");

  private final String label;
  private final String messageCode;

  StreamCreationType(
      final String label,
      final String messageCode) {
    this.label = label;
    this.messageCode = messageCode;
  }

  public static StreamCreationType of(final String value) {
    return parseEnumOrNull(value, StreamCreationType.class);
  }

}
