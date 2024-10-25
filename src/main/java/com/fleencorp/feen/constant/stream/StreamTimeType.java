package com.fleencorp.feen.constant.stream;

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

  LIVE("Live"),
  PAST("Past"),
  UPCOMING("Upcoming");

  private final String value;

  StreamTimeType(final String value) {
    this.value = value;
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
