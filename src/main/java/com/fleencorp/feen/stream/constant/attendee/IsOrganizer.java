package com.fleencorp.feen.stream.constant.attendee;

import com.fleencorp.base.constant.base.ApiParameter;
import lombok.Getter;

/**
 * Enum representing the attendance status of a person.
 * This enum contains two values: {@link IsOrganizer#NO} and {@link IsOrganizer#YES},
 * which correspond to the two possible states of attendance.
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
@Getter
public enum IsOrganizer implements ApiParameter {

  NO("No", "is.organizer.no", "is.organizer.no.2"),
  YES("Yes", "is.organizer.yes", "is.organizer.yes.2");

  private final String value;
  private final String messageCode;
  private final String messageCode2;

  IsOrganizer(
      final String value,
      final String messageCode,
      final String messageCode2) {
    this.value = value;
    this.messageCode = messageCode;
    this.messageCode2 = messageCode2;
  }

  /**
   * Returns the corresponding {@link IsOrganizer} value based on the boolean input.
   * This method maps a boolean value to an enum constant, where {@code true} maps to {@link IsOrganizer#YES}
   * and {@code false} maps to {@link IsOrganizer#NO}.
   *
   * @param isOrganizer a boolean indicating whether the person is a organizer.
   * @return {@link IsOrganizer#YES} if the input is {@code true}, {@link IsOrganizer#NO} if the input is {@code false}.
   */
  public static IsOrganizer by(final boolean isOrganizer) {
    return isOrganizer ? YES : NO;
  }
}
