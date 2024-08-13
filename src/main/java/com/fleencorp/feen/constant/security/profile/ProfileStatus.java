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
}
