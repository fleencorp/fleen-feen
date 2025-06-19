package com.fleencorp.feen.block.user.constant;

import com.fleencorp.base.constant.base.ApiParameter;
import lombok.Getter;

@Getter
public enum IsBlocked implements ApiParameter {

  NO("No", "is.blocked.no", "is.blocked.no.2", "is.blocked.no.otherText"),
  YES("Yes", "is.blocked.yes", "is.blocked.yes.2", "is.blocked.yes.otherText");

  private final String value;
  private final String messageCode;
  private final String messageCode2;
  private final String messageCode3;

  IsBlocked(
      final String value,
      final String messageCode,
      final String messageCode2,
      final String messageCode3) {
    this.value = value;
    this.messageCode = messageCode;
    this.messageCode2 = messageCode2;
    this.messageCode3 = messageCode3;
  }

  /**
   * Returns the {@link IsBlocked} status based on the given boolean value.
   *
   * <p>This method checks if the provided {@code isBlocked} value is true or false. If true, it returns {@link IsBlocked#YES},
   * otherwise it returns {@link IsBlocked#NO}.</p>
   *
   * @param isBlocked the boolean value indicating whether the entity is blocked
   * @return {@link IsBlocked#YES} if {@code isBlocked} is true, otherwise {@link IsBlocked#NO}
   */
  public static IsBlocked by(final boolean isBlocked) {
    return isBlocked ? YES : NO;
  }
}

