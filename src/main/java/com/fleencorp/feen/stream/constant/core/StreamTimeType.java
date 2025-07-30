package com.fleencorp.feen.stream.constant.core;

import com.fleencorp.base.constant.base.ApiParameter;
import lombok.Getter;

/**
* Enum representing different types of streams.
*
* @author Yusuf Alamu Musa
* @version 1.0
*/
@Getter
public enum StreamTimeType implements ApiParameter {

  LIVE("Live", "stream.time.type.live"),
  PAST("Past", "stream.time.type.past"),
  UPCOMING("Upcoming", "stream.time.type.upcoming");

  private final String value;
  private final String messageCode;

  StreamTimeType(
      final String value,
      final String messageCode) {
    this.value = value;
    this.messageCode = messageCode;
  }

  /**
   * Checks if the given stream time type is upcoming.
   * This method evaluates whether the specified {@link StreamTimeType} is
   * of type {@code UPCOMING}.
   *
   * @param streamTimeType the {@link StreamTimeType} to be checked
   * @return {@code true} if the {@code streamTimeType} is {@code UPCOMING};
   *         {@code false} otherwise
   */
  public static boolean isUpcoming(final StreamTimeType streamTimeType) {
    return streamTimeType == UPCOMING;
  }

  /**
   * Checks if the given stream time type is past.
   * This method evaluates whether the specified {@link StreamTimeType} is
   * of type {@code PAST}.
   *
   * @param streamTimeType the {@link StreamTimeType} to be checked
   * @return {@code true} if the {@code streamTimeType} is {@code PAST};
   *         {@code false} otherwise
   */
  public static boolean isPast(final StreamTimeType streamTimeType) {
    return streamTimeType == PAST;
  }
}
