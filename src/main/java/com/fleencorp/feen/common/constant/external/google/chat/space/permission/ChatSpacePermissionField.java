package com.fleencorp.feen.common.constant.external.google.chat.space.permission;

import com.fleencorp.base.constant.base.ApiParameter;
import lombok.Getter;

/**
 * Enum representing the various permission fields available for chat spaces.
 * This enum implements {@link ApiParameter} to provide a standardized way
 * to handle API parameter values related to chat space permissions.
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
@Getter
public enum ChatSpacePermissionField implements ApiParameter {

  PERMISSION_SETTINGS("permissionSettings"),
  MANAGE_MEMBERS_AND_GROUPS("manageMembersAndGroups"),
  MODIFY_SPACE_DETAILS("modifySpaceDetails"),
  TOGGLE_HISTORY("toggleHistory"),
  USE_AT_MENTION_AT_ALL("useMentionAtAll"),
  MANAGE_APPS("manageApps"),
  MANAGE_WEBHOOKS("manageWebhooks"),
  POST_MESSAGES("postMessages"),
  REPLY_MESSAGES("replyMessages");

  private final String value;

  ChatSpacePermissionField(final String value) {
    this.value = value;
  }

  /**
   * Retrieves the value associated with the MANAGE_MEMBERS_AND_GROUPS permission setting.
   *
   * @return The value representing the permission to manage members and groups in the chat space.
   */
  public static String manageMembersAndGroups() {
    return MANAGE_MEMBERS_AND_GROUPS.getValue();
  }

  /**
   * Retrieves the value associated with the MODIFY_SPACE_DETAILS permission setting.
   *
   * @return The value representing the permission to modify details of the chat space.
   */
  public static String modifySpaceDetails() {
    return MODIFY_SPACE_DETAILS.getValue();
  }

  /**
   * Retrieves the value associated with the TOGGLE_HISTORY permission setting.
   *
   * @return The value representing the permission to toggle chat history for the chat space.
   */
  public static String toggleHistory() {
    return TOGGLE_HISTORY.getValue();
  }

  /**
   * Retrieves the value associated with the USE_AT_MENTION_AT_ALL permission setting.
   *
   * @return The value representing the permission to use @mentions for all members in the chat space.
   */
  public static String useAtMentionAll() {
    return USE_AT_MENTION_AT_ALL.getValue();
  }

  /**
   * Retrieves the value associated with the MANAGE_APPS permission setting.
   *
   * @return The value representing the permission to manage applications integrated with the chat space.
   */
  public static String manageApps() {
    return MANAGE_APPS.getValue();
  }

  /**
   * Retrieves the value associated with the MANAGE_WEBHOOKS permission setting.
   *
   * @return The value representing the permission to manage webhooks associated with the chat space.
   */
  public static String manageWebhooks() {
    return MANAGE_WEBHOOKS.getValue();
  }

  /**
   * Retrieves the value associated with the REPLY_MESSAGES permission setting.
   *
   * @return The value representing the permission to reply to messages in the chat space.
   */
  public static String replyMessages() {
    return REPLY_MESSAGES.getValue();
  }

}
