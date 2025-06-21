package com.fleencorp.feen.link.constant;

import com.fleencorp.base.constant.base.ApiParameter;
import lombok.Getter;

import static com.fleencorp.base.util.EnumUtil.parseEnumOrNull;

@Getter
public enum LinkParentType implements ApiParameter {

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

  public static boolean isChatSpace(final String value) {
    return of(value) == CHAT_SPACE;
  }

  public static boolean isStream(final String value) {
    return of(value) == STREAM;
  }
}
