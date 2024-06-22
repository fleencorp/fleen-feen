package com.fleencorp.feen.constant.security.profile;

import com.fleencorp.feen.constant.base.ApiParameter;
import lombok.Getter;

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
  INACTIVE("Inactive"),
  DISABLED("Disabled"),
  BANNED("Banned");

  private final String value;

  ProfileStatus(String value) {
    this.value = value;
  }
}
