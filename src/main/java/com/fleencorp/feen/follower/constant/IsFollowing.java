package com.fleencorp.feen.follower.constant;

import com.fleencorp.base.constant.base.ApiParameter;
import lombok.Getter;

@Getter
public enum IsFollowing implements ApiParameter {

  NO("No", "is.following.no", "is.following.no.2", "is.following.no.3", "is.following.no.otherText"),
  YES("Yes", "is.following.yes", "is.following.yes.2", "is.following.yes.3", "is.following.yes.otherText");

  private final String value;
  private final String messageCode;
  private final String messageCode2;
  private final String messageCode3;
  private final String messageCode4;

  IsFollowing(
      final String value,
      final String messageCode,
      final String messageCode2,
      final String messageCode3,
      final String messageCode4) {
    this.value = value;
    this.messageCode = messageCode;
    this.messageCode2 = messageCode2;
    this.messageCode3 = messageCode3;
    this.messageCode4 = messageCode4;
  }

  /**
   * Returns the {@link IsFollowing} status based on the given boolean value.
   *
   * <p>This method checks if the provided {@code isFollowing} value is true or false. If true, it returns {@link IsFollowing#YES},
   * otherwise it returns {@link IsFollowing#NO}.</p>
   *
   * @param isFollowing the boolean value indicating whether the entity is following
   * @return {@link IsFollowing#YES} if {@code isFollowing} is true, otherwise {@link IsFollowing#NO}
   */
  public static IsFollowing by(final boolean isFollowing) {
    return isFollowing ? YES : NO;
  }
}

