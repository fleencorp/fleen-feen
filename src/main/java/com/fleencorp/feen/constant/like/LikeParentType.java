package com.fleencorp.feen.constant.like;

import com.fleencorp.base.constant.base.ApiParameter;
import lombok.Getter;

import static com.fleencorp.base.util.EnumUtil.parseEnumOrNull;

/**
 * Represents the parent entity type that a like can be associated with.
 *
 * <p>This enum supports two types: {@code STREAM} and {@code CHAT_SPACE}.
 * These are used to distinguish the source of a like operation such as
 * a live stream or a chat space.</p>
 *
 * <p>Utility methods like {@code isStream()} and {@code isChatSpace()}
 * allow for simple conditional checks on the enum value.</p>
 */
@Getter
public enum LikeParentType implements ApiParameter {

  CHAT_SPACE("Chat Space"),
  REVIEW("Review"),
  STREAM("Stream");

  private final String value;

  LikeParentType(final String value) {
    this.value = value;
  }

  public static LikeParentType of(final String value) {
    return parseEnumOrNull(value, LikeParentType.class);
  }

  public static boolean isStream(final LikeParentType likeParentType) {
    return likeParentType == STREAM;
  }

  public static boolean isChatSpace(final LikeParentType likeParentType) {
    return likeParentType == CHAT_SPACE;
  }

  public static boolean isReview(final LikeParentType likeParentType) {
    return likeParentType == REVIEW;
  }
}
