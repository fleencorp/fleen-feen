package com.fleencorp.feen.constant.stream;

import com.fleencorp.base.constant.base.ApiParameter;
import lombok.Getter;

/**
 * Enum representing the attendance status of a person.
 * This enum contains two values: {@link IsAttending#NO} and {@link IsAttending#YES},
 * which correspond to the two possible states of attendance.
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
@Getter
public enum IsAttending implements ApiParameter {

  NO("No", "is.attending.no"),
  YES("Yes", "is.attending.yes");

  private final String value;
  private final String messageCode;

  IsAttending(
      final String value,
      final String messageCode) {
    this.value = value;
    this.messageCode = messageCode;
  }

  /**
   * Returns the corresponding {@link IsAttending} value based on the boolean input.
   * This method maps a boolean value to an enum constant, where {@code true} maps to {@link IsAttending#YES}
   * and {@code false} maps to {@link IsAttending#NO}.
   *
   * @param isAttending a boolean indicating whether the person is attending or not.
   * @return {@link IsAttending#YES} if the input is {@code true}, {@link IsAttending#NO} if the input is {@code false}.
   */
  public static IsAttending by(final boolean isAttending) {
    return isAttending ? YES : NO;
  }
}
