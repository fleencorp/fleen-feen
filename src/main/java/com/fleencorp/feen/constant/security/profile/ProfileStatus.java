package com.fleencorp.feen.constant.security.profile;

import com.fleencorp.base.constant.base.ApiParameter;
import lombok.Getter;

import static com.fleencorp.base.util.EnumUtil.parseEnumOrNull;

/**
* Enumeration for Profile Status types.
*
* <p>This enum defines the various status states a user profile can have.
* Each enum constant is associated with a string value that represents the profile status.</p>
*
* @author Yusuf Alamu Musa
* @version 1.0
*/
@Getter
public enum ProfileStatus implements ApiParameter {

  ACTIVE("Active"),
  BANNED("Banned"),
  DISABLED("Disabled"),
  INACTIVE("Inactive");

  private final String value;

  ProfileStatus(final String value) {
    this.value = value;
  }

  public static ProfileStatus of(final String value) {
    return parseEnumOrNull(value, ProfileStatus.class);
  }

  /**
   * Checks if the specified {@link ProfileStatus} is DISABLED.
   *
   * @param profileStatus the profile status to check
   * @return {@code true} if the {@code profileStatus} is DISABLED; {@code false} otherwise
   */
  public static boolean isDisabled(final ProfileStatus profileStatus) {
    return profileStatus == DISABLED;
  }

  /**
   * Checks if the specified {@link ProfileStatus} is BANNED.
   *
   * @param profileStatus the profile status to check
   * @return {@code true} if the {@code profileStatus} is BANNED; {@code false} otherwise
   */
  public static boolean isBanned(final ProfileStatus profileStatus) {
    return profileStatus == BANNED;
  }

  /**
   * Checks if the specified {@link ProfileStatus} is INACTIVE.
   *
   * @param profileStatus the profile status to check
   * @return {@code true} if the {@code profileStatus} is INACTIVE; {@code false} otherwise
   */
  public static boolean isInactive(final ProfileStatus profileStatus) {
    return profileStatus == INACTIVE;
  }

}
