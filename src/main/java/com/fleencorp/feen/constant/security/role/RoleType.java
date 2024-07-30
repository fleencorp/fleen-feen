package com.fleencorp.feen.constant.security.role;

import lombok.Getter;

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

  SUPER_ADMINISTRATOR("Super Administrator"),
  ADMINISTRATOR("Administrator"),
  REFRESH_TOKEN_USER("Refresh Token User"),
  PRE_VERIFIED_USER("Pre Verified User"),
  PRE_AUTHENTICATED_USER("Pre Authenticated User"),
  USER("User"),
  RESET_PASSWORD_USER("Reset Password User");

  private final String value;

  RoleType(final String value) {
        this.value = value;
    }
}
