package com.fleencorp.feen.constant.security.verification;

import com.fleencorp.base.constant.base.ApiParameter;
import lombok.Getter;

/**
* Enumeration for Verification Type.
*
* <p>This enum defines the different types of verification methods.
* Each enum constant is associated with a string value that represents the verification type.</p>
*
* @author Yusuf Alamu Musa
* @version 1.0
*/
@Getter
public enum VerificationType implements ApiParameter {

  PHONE("PHONE"),
  EMAIL("Email");

  private final String value;

  VerificationType(final String value) {
    this.value = value;
  }
}
