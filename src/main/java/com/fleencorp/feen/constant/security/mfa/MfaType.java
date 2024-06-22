package com.fleencorp.feen.constant.security.mfa;

import com.fleencorp.feen.constant.base.ApiParameter;
import lombok.Getter;

/**
 * Enumeration for Multi-Factor Authentication (MFA) types.
 *
 * <p>This enum defines the various types of MFA methods available.
 * Each enum constant is associated with a string value that represents the MFA type.</p>
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
@Getter
public enum MfaType implements ApiParameter {

  PHONE("PHONE"),
  EMAIL("Email"),
  AUTHENTICATOR("Authenticator"),
  NONE("None");

  private final String value;

  MfaType(String value) {
    this.value = value;
  }
}
