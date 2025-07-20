package com.fleencorp.feen.common.util;

public class CommonUtil {

  private CommonUtil() {}

  /**
   * Checks whether all provided values are non-null.
   *
   * <p>Returns {@code true} only if none of the values in the input array are {@code null}.
   * If at least one value is {@code null}, this method returns {@code false}.</p>
   *
   * @param values the values to check
   * @return {@code true} if all values are non-null, {@code false} otherwise
   */
  public static boolean allNonNull(final Object... values) {
    for (final Object value : values) {
      if (value == null) {
        return false;
      }
    }
    return true;
  }

}
