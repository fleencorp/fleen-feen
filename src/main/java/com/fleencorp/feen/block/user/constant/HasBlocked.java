package com.fleencorp.feen.block.user.constant;

import lombok.Getter;

@Getter
public enum HasBlocked {

  NO("No", "has.blocked.no", "has.blocked.no.2", "has.blocked.no.otherText"),
  YES("Yes", "has.blocked.yes", "has.blocked.yes.2", "has.blocked.yes.otherText");

  private final String label;
  private final String messageCode;
  private final String messageCode2;
  private final String messageCode3;

  HasBlocked(
      final String label,
      final String messageCode,
      final String messageCode2,
      final String messageCode3) {
    this.label = label;
    this.messageCode = messageCode;
    this.messageCode2 = messageCode2;
    this.messageCode3 = messageCode3;
  }

  /**
   * Returns the {@link HasBlocked} status based on the given boolean value.
   *
   * <p>This method checks if the provided {@code isBlocked} value is true or false. If true, it returns {@link HasBlocked#YES},
   * otherwise it returns {@link HasBlocked#NO}.</p>
   *
   * @param isBlocked the boolean value indicating whether the entity is blocked
   * @return {@link HasBlocked#YES} if {@code isBlocked} is true, otherwise {@link HasBlocked#NO}
   */
  public static HasBlocked by(final boolean isBlocked) {
    return isBlocked ? YES : NO;
  }
}

