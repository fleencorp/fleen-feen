package com.fleencorp.feen.stream.constant.core;

import com.fleencorp.base.constant.base.ApiParameter;
import lombok.Getter;

import static com.fleencorp.base.util.EnumUtil.parseEnumOrNull;

/**
* Enum representing the visibility status of a stream.
*
* @author Yusuf Alamu Musa
* @version 1.0
*/
@Getter
public enum StreamVisibility implements ApiParameter {

  PRIVATE("private", "stream.visibility.private"),
  PROTECTED("protected", "stream.visibility.protected"),
  PUBLIC("public", "stream.visibility.public");

  private final String value;
  private final String messageCode;

  StreamVisibility(
      final String value,
      final String messageCode) {
    this.value = value;
    this.messageCode = messageCode;
  }

  public static StreamVisibility of(final String value) {
    return parseEnumOrNull(value, StreamVisibility.class);
  }

  /**
   * Checks if the specified {@link StreamVisibility} is either PRIVATE or PROTECTED.
   *
   * @param streamVisibility the stream visibility to check
   * @return {@code true} if the {@code streamVisibility} is either PRIVATE or PROTECTED; {@code false} otherwise
   */
  public static boolean isPrivateOrProtected(final StreamVisibility streamVisibility) {
    return streamVisibility == PRIVATE || streamVisibility == PROTECTED;
  }

  /**
   * Checks if the specified {@link StreamVisibility} is PUBLIC.
   *
   * @param streamVisibility the stream visibility to check
   * @return {@code true} if the {@code streamVisibility} is PUBLIC; {@code false} otherwise
   */
  public static boolean isPublic(final StreamVisibility streamVisibility) {
    return streamVisibility == PUBLIC;
  }

  /**
   * Checks if the given visibility indicates a private or protected status.
   *
   * @param visibility The visibility status to check.
   * @return true if the visibility is either private or protected, false otherwise.
   */
  public static boolean isPrivateOrProtected(final String visibility) {
    return PRIVATE.getValue().equalsIgnoreCase(visibility) ||
      PROTECTED.getValue().equalsIgnoreCase(visibility);
  }

  /**
   * Checks if the given visibility indicates a public status.
   *
   * @param visibility The visibility status to check.
   * @return true if the visibility is public, false otherwise.
   */
  public static boolean isPublic(final String visibility) {
    return PUBLIC.getValue().equalsIgnoreCase(visibility);
  }

  public static boolean isPrivate(final StreamVisibility visibility) {
    return visibility == PRIVATE;
  }

}
