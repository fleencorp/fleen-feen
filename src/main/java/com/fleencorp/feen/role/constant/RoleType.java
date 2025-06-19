package com.fleencorp.feen.role.constant;

import lombok.Getter;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
* Enumeration for Role Types.
*
* <p>This enum defines various role types that can be assigned to users in the system.
* Each role type is associated with a descriptive string value.</p>
*
* @author Yusuf Alamu Musa
* @version 1.0
*/
@Getter
public enum RoleType {

  SUPER_ADMINISTRATOR("Super Administrator", "role.type.super.administrator"),
  ADMINISTRATOR("Administrator", "role.type.administrator"),
  REFRESH_TOKEN_USER("Refresh Token User", "role.type.refresh.token.user"),
  PRE_VERIFIED_USER("Pre Verified User", "role.type.pre.verified.user"),
  PRE_AUTHENTICATED_USER("Pre Authenticated User", "role.type.pre.authenticated.user"),
  USER("User", "role.type.user"),
  RESET_PASSWORD_USER("Reset Password User", "role.type.reset.password.user"),;

  private final String value;
  private final String messageCode;

  RoleType(
        final String value,
        final String messageCode) {
      this.value = value;
      this.messageCode = messageCode;
    }

  /**
   * Checks if the specified {@link RoleType} is PRE_VERIFIED_USER.
   *
   * @param roleType the role type to check
   * @return {@code true} if the {@code roleType} is PRE_VERIFIED_USER; {@code false} otherwise
   */
  public static boolean isPreVerified(final RoleType roleType) {
    return PRE_VERIFIED_USER == roleType;
  }

  /**
   * Returns the default user roles as a set of role names.
   *
   * <p>This method retrieves the default roles for new users, such as {@code PRE_VERIFIED_USER},
   * and converts them into a set of their string names.</p>
   *
   * @return a set of default role names for a new user
   */
  public static Set<String> getDefaultUserRoles() {
    return Stream.of(PRE_VERIFIED_USER)
      .map(RoleType::name)
      .collect(Collectors.toSet());
  }

}
