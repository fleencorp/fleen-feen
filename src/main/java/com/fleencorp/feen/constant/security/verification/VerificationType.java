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

  EMAIL("Email", "verification.type.email"),
  PHONE("PHONE", "verification.type.phone");

  private final String value;
  private final String messageCode;

  VerificationType(
      final String value,
      final String messageCode) {
    this.value = value;
    this.messageCode = messageCode;
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
