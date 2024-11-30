package com.fleencorp.feen.constant.stream;

import com.fleencorp.base.constant.base.ApiParameter;
import lombok.Getter;

/**
 * Represents the deletion status of an entity.
 *
 * <p>The {@link IsDeleted} enum provides two possible values: {@link IsDeleted#YES} and {@link IsDeleted#NO},
 * indicating whether an entity is deleted or not. It implements the {@link ApiParameter} interface for usage in
 * API-related contexts where deletion status is required as a parameter.</p>
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
@Getter
public enum IsDeleted implements ApiParameter {

  NO("No", "is.deleted.no", "is.deleted.no.2"),
  YES("Yes", "is.deleted.yes", "is.deleted.yes.2");

  private final String value;
  private final String messageCode;
  private final String messageCode2;

  IsDeleted(
      final String value,
      final String messageCode,
      final String messageCode2) {
    this.value = value;
    this.messageCode = messageCode;
    this.messageCode2 = messageCode2;
  }

  /**
   * Returns the {@link IsDeleted} status based on the given boolean value.
   *
   * <p>This method checks if the provided {@code isDeleted} value is true or false. If true, it returns {@link IsDeleted#YES},
   * otherwise it returns {@link IsDeleted#NO}.</p>
   *
   * @param isDeleted the boolean value indicating whether the entity is deleted
   * @return {@link IsDeleted#YES} if {@code isDeleted} is true, otherwise {@link IsDeleted#NO}
   */
  public static IsDeleted by(final boolean isDeleted) {
    return isDeleted ? YES : NO;
  }
}
