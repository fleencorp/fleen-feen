package com.fleencorp.feen.business.constant;

import lombok.Getter;

import static com.fleencorp.base.util.EnumUtil.parseEnumOrNull;

@Getter
public enum BusinessChannelType {

  OFFLINE("Offline", "business.channel.type.offline", "business.channel.type.offline.2"),
  ONLINE("Online", "business.channel.type.online", "business.channel.type.online.2"),
  OFFLINE_AND_ONLINE("Offline & Online", "business.channel.type.offline.online", "business.channel.type.offline.online.2");

  private final String label;
  private final String messageCode;
  private final String messageCode2;

  BusinessChannelType(
      final String label,
      final String messageCode,
      final String messageCode2) {
    this.label = label;
    this.messageCode = messageCode;
    this.messageCode2 = messageCode2;
  }

  public static BusinessChannelType of(final String value) {
    return parseEnumOrNull(value, BusinessChannelType.class);
  }
}

