package com.fleencorp.feen.follower.constant;

import com.fleencorp.base.constant.base.ApiParameter;
import lombok.Getter;

@Getter
public enum IsFollowed implements ApiParameter {

  NO("No", "is.followed.no", "is.followed.no.2", "is.followed.no.3", "is.followed.no.otherText"),
  YES("Yes", "is.followed.yes", "is.followed.yes.2", "is.followed.yes.3", "is.followed.yes.otherText");

  private final String value;
  private final String messageCode;
  private final String messageCode2;
  private final String messageCode3;
  private final String messageCode4;

  IsFollowed(
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
   * Returns the {@link IsFollowed} status based on the given boolean value.
   *
   * <p>This method checks if the provided {@code isFollowed} value is true or false. If true, it returns {@link IsFollowed#YES},
   * otherwise it returns {@link IsFollowed#NO}.</p>
   *
   * @param isFollowed the boolean value indicating whether the entity is followed
   * @return {@link IsFollowed#YES} if {@code isFollowed} is true, otherwise {@link IsFollowed#NO}
   */
  public static IsFollowed by(final boolean isFollowed) {
    return isFollowed ? YES : NO;
  }
}

