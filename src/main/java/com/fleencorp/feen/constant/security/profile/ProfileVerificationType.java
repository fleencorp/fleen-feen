package com.fleencorp.feen.constant.security.profile;

import com.fleencorp.base.constant.base.ApiParameter;
import lombok.Getter;

/**
* Enumeration for Profile Verification Type.
*
* <p>This enum defines the different types of profile verification methods.
* Each enum constant is associated with a string value that represents the verification type.</p>
*
* @author Yusuf Alamu Musa
* @version 1.0
*/
@Getter
public enum ProfileVerificationType implements ApiParameter {

  PHONE("PHONE"),
  EMAIL("Email");

  private final String value;

  ProfileVerificationType(String value) {
    this.value = value;
  }
}
