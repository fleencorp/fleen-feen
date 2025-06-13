package com.fleencorp.feen.like.constant;

import com.fleencorp.base.constant.base.ApiParameter;
import lombok.Getter;

import static com.fleencorp.base.util.EnumUtil.parseEnumOrNull;

/**
 * Represents the type of like action a user can perform.
 *
 * <p>This enum currently supports two actions:
 * {@code LIKE} and {@code UNLIKE}. These indicate whether
 * a user has liked or unliked a given entity such as a
 * stream or chat space.</p>
 *
 * <p>Helper methods such as {@code isLike()} and {@code liked()}
 * are provided to simplify conditional logic related to the like state.</p>
 */
@Getter
public enum LikeType implements ApiParameter {

  LIKE("Like"),
  UNLIKE("Unlike");

  private final String value;

  LikeType(final String value) {
    this.value = value;
  }

  public static LikeType of(final String value) {
    return parseEnumOrNull(value, LikeType.class);
  }

  public static boolean isLike(final LikeType likeType) {
    return LIKE == likeType;
  }

  public static boolean liked(final LikeType likeType) {
    return LIKE == likeType;
  }
}
