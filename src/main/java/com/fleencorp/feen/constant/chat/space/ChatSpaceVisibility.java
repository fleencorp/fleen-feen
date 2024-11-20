package com.fleencorp.feen.constant.chat.space;

import com.fleencorp.base.constant.base.ApiParameter;
import lombok.Getter;

import static com.fleencorp.base.util.EnumUtil.parseEnumOrNull;

/**
 * Enum representing the visibility statuses of a chat space.
 *
 * <p>This enum defines the possible visibility levels for a chat space,
 * determining who can view or join the space.</p>
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
@Getter
public enum ChatSpaceVisibility implements ApiParameter {

  PRIVATE("private", "chat.space.visibility.private"),
  PUBLIC("public", "chat.space.visibility.public");

  private final String value;
  private final String messageCode;

  ChatSpaceVisibility(
      final String value,
      final String messageCode) {
    this.value = value;
    this.messageCode = messageCode;
  }

  /**
   * Parses the provided string value into a corresponding {@link ChatSpaceVisibility} enum.
   * Returns null if the value does not match any valid enum constant.
   *
   * @param value the string representation of the visibility level
   * @return the corresponding {@link ChatSpaceVisibility} enum, or null if no match is found
   */
  public static ChatSpaceVisibility of(final String value) {
    return parseEnumOrNull(value, ChatSpaceVisibility.class);
  }

  /**
   * Checks if the specified chat space visibility is private.
   *
   * @param visibility the visibility status of the chat space
   * @return true if private, false otherwise
   */
  public static boolean isPrivate(final ChatSpaceVisibility visibility) {
    return visibility == PRIVATE;
  }

  /**
   * Checks if the specified chat space visibility is public.
   *
   * @param visibility the visibility status of the chat space
   * @return true if private, false otherwise
   */
  public static boolean isPublic(final ChatSpaceVisibility visibility) {
    return visibility == PUBLIC;
  }

}
