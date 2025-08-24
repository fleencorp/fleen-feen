package com.fleencorp.feen.link.constant;

import lombok.Getter;

import static com.fleencorp.base.util.EnumUtil.parseEnumOrNull;

@Getter
public enum LinkParentType {

  BUSINESS("Business"),
  CHAT_SPACE("Chat Space"),
  STREAM("Stream"),
  USER("User");

  private final String value;

  LinkParentType(final String value) {
    this.value = value;
  }

  public static LinkParentType of(final String value) {
    return parseEnumOrNull(value, LinkParentType.class);
  }

  public static boolean isBusiness(final LinkParentType type) {
    return type == BUSINESS;
  }

  public static boolean isChatSpace(final LinkParentType type) {
    return type == CHAT_SPACE;
  }

  public static boolean isStream(final LinkParentType type) {
    return type == STREAM;
  }
}
