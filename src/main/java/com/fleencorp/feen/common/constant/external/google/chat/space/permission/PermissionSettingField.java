package com.fleencorp.feen.common.constant.external.google.chat.space.permission;

import com.fleencorp.base.constant.base.ApiParameter;
import lombok.Getter;

/**
 * Enum representing the fields for permission settings within the application.
 *
 * <p>This enum implements the {@link ApiParameter} interface and defines the available fields
 * that determine the permissions granted to different user roles within a chat space or similar context.</p>
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
@Getter
public enum PermissionSettingField implements ApiParameter {

  MANAGERS_ALLOWED("managersAllowed"),
  MEMBERS_ALLOWED("membersAllowed");

  private final String value;

  PermissionSettingField(final String value) {
    this.value = value;
  }

  /**
   * Retrieves the value indicating that managers are allowed to perform certain actions.
   *
   * @return A string representation of the managers' allowed permission setting.
   */
  public static String managersAllowed() {
    return MANAGERS_ALLOWED.getValue();
  }

  /**
   * Retrieves the value indicating that members are allowed to perform certain actions.
   *
   * @return A string representation of the members' allowed permission setting.
   */
  public static String membersAllowed() {
    return MEMBERS_ALLOWED.getValue();
  }
}
