package com.fleencorp.feen.like.constant;

import lombok.Getter;

import static com.fleencorp.base.util.EnumUtil.parseEnumOrNull;


@Getter
public enum LikeType {

  LIKE("Like"),
  UNLIKE("Unlike");

  private final String label;

  LikeType(final String label) {
    this.label = label;
  }

  public static LikeType of(final String value) {
    return parseEnumOrNull(value, LikeType.class);
  }

  public static boolean isLiked(final LikeType likeType) {
    return LIKE == likeType;
  }
}
