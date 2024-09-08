package com.fleencorp.feen.constant.security.verification;

import com.fleencorp.base.constant.base.ApiParameter;
import lombok.Getter;

import static com.fleencorp.base.util.EnumUtil.parseEnumOrNull;

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

  public static VerificationType of(final String value) {
    return parseEnumOrNull(value, VerificationType.class);
  }

  /**
   * Checks if the given verification type is an email verification.
   *
   * @param verificationType the verification type to check
   * @return {@code true} if the verification type is email; {@code false} otherwise
   */
  public static boolean isEmail(final VerificationType verificationType) {
    return EMAIL == verificationType;
  }

  /**
   * Checks if the given verification type is a phone verification.
   *
   * @param verificationType the verification type to check
   * @return {@code true} if the verification type is phone; {@code false} otherwise
   */
  public static boolean isPhone(final VerificationType verificationType) {
    return PHONE == verificationType;
  }

}
