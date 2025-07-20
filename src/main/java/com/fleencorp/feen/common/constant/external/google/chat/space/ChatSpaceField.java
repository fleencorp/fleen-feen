package com.fleencorp.feen.common.constant.external.google.chat.space;

import com.fleencorp.base.constant.base.ApiParameter;
import lombok.Getter;

/**
 * Enumeration representing the fields associated with a chat space.
 *
 * <p>This enum defines constants for various fields related to chat spaces,
 * which can be used for accessing specific attributes within chat space data.</p>
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
@Getter
public enum ChatSpaceField implements ApiParameter {

  CREATE_TIME("createTime"),
  LAST_ACTIVE_TIME("lastActiveTime"),
  SPACE_URI("spaceUri"),
  MEMBERSHIP_COUNT("membershipCount"),
  JOINED_DIRECT_HUMAN_USER_COUNT("joinedDirectHumanUserCount"),
  JOINED_GROUP_COUNT("joinedGroupCount"),
  SPACE_HISTORY_STATE("spaceHistoryState"),
  ROLE("role"),
  DISPLAY_NAME("displayName"),
  SPACE_DETAILS_DESCRIPTION("spaceDetails.description"),
  SPACE_DETAILS_GUIDELINES("spaceDetails.guidelines"),
  SPACE_DETAILS("spaceDetails"),;

  private final String value;

  ChatSpaceField(final String value) {
    this.value = value;
  }

  /**
   * Retrieves the string representing membership count.
   *
   * @return the value of {@code MEMBERSHIP_COUNT}
   */
  public static String membershipCount() {
    return MEMBERSHIP_COUNT.getValue();
  }

  /**
   * Retrieves the string representing the count of direct human users joined.
   *
   * @return the value of {@code JOINED_DIRECT_HUMAN_USER_COUNT}
   */
  public static String joinedDirectHumanUserCount() {
    return JOINED_DIRECT_HUMAN_USER_COUNT.getValue();
  }

  /**
   * Retrieves the string representing the count of joined groups.
   *
   * @return the value of {@code JOINED_GROUP_COUNT}
   */
  public static String joinedGroupCount() {
    return JOINED_GROUP_COUNT.getValue();
  }

  /**
   * Retrieves the string representing the chat space's history state.
   *
   * @return the value of {@code SPACE_HISTORY_STATE}
   */
  public static String spaceHistoryState() {
    return SPACE_HISTORY_STATE.getValue();
  }

  /**
   * Retrieves the string representing the role of a member in a chat space.
   *
   * @return the value of {@code ROLE}
   */
  public static String role() {
    return ROLE.getValue();
  }

  /**
   * Retrieves the string representing the display name of a chat space.
   *
   * @return the value of {@code DISPLAY_NAME}
   */
  public static String displayName() {
    return DISPLAY_NAME.getValue();
  }

  /**
   * Retrieves the string representing the description of the chat space.
   *
   * @return the value of {@code SPACE_DETAILS_DESCRIPTION}
   */
  public static String description() {
    return SPACE_DETAILS_DESCRIPTION.getValue();
  }

  /**
   * Retrieves the string representing the chat space details.
   *
   * @return the value of {@code SPACE_DETAILS}
   */
  public static String spaceDetails() {
    return SPACE_DETAILS.getValue();
  }
}
