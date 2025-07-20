package com.fleencorp.feen.stream.constant.attendee;

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

  NO("No", "is.a.speaker.no", "is.a.speaker.no.2"),
  YES("Yes", "is.a.speaker.yes", "is.a.speaker.yes.2"),;

  private final String value;
  private final String messageCode;
  private final String messageCode2;

  IsASpeaker(
      final String value,
      final String messageCode,
      final String messageCode2) {
    this.value = value;
    this.messageCode = messageCode;
    this.messageCode2 = messageCode2;
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
