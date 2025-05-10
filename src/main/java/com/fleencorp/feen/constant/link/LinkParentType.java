package com.fleencorp.feen.constant.link;

import com.fleencorp.base.constant.base.ApiParameter;
import lombok.Getter;

import static com.fleencorp.base.util.EnumUtil.parseEnumOrNull;

@Getter
public enum LinkParentType implements ApiParameter {

  STREAM("Stream"),
  CHAT_SPACE("Chat Space");

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
