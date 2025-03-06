package com.fleencorp.feen.constant.stream.attendee;

import com.fleencorp.base.constant.base.ApiParameter;
import lombok.Getter;

/**
 * Enum representing the attendance status of a person.
 * This enum contains two values: {@link IsASpeaker#NO} and {@link IsASpeaker#YES},
 * which correspond to the two possible states of attendance.
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
@Getter
public enum IsASpeaker implements ApiParameter {

  NO("No", "is.a.speaker.no"),
  YES("Yes", "is.a.speaker.yes");

  private final String value;
  private final String messageCode;

  IsASpeaker(
      final String value,
      final String messageCode) {
    this.value = value;
    this.messageCode = messageCode;
  }

  /**
   * Returns the corresponding {@link IsASpeaker} value based on the boolean input.
   * This method maps a boolean value to an enum constant, where {@code true} maps to {@link IsASpeaker#YES}
   * and {@code false} maps to {@link IsASpeaker#NO}.
   *
   * @param isASpeaker a boolean indicating whether the person is a speaker.
   * @return {@link IsASpeaker#YES} if the input is {@code true}, {@link IsASpeaker#NO} if the input is {@code false}.
   */
  public static IsASpeaker by(final boolean isASpeaker) {
    return isASpeaker ? YES : NO;
  }
}
