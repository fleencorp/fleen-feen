package com.fleencorp.feen.mfa.constant;

import com.fleencorp.base.constant.base.ApiParameter;
import lombok.Getter;

import static com.fleencorp.base.util.EnumUtil.parseEnumOrNull;

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

  PHONE("PHONE", "mfa.type.phone"),
  EMAIL("Email", "mfa.type.email"),
  AUTHENTICATOR("Authenticator", "mfa.type.authenticator"),
  NONE("None", "mfa.type.none"),;

  private final String value;
  private final String messageCode;

  MfaType(
      final String value,
      final String messageCode) {
    this.value = value;
    this.messageCode = messageCode;
  }

  public static MfaType of(final String value) {
    return parseEnumOrNull(value, MfaType.class);
  }

  /**
   * Checks if the specified {@link MfaType} is either PHONE or EMAIL.
   *
   * @param mfaType the MFA type to check
   * @return {@code true} if the {@code mfaType} is PHONE or EMAIL; {@code false} otherwise
   */
  public static boolean isPhoneOrEmail(final MfaType mfaType) {
    return mfaType == PHONE || mfaType == EMAIL;
  }

  /**
   * Checks if the specified {@link MfaType} is NONE.
   *
   * @param mfaType the MFA type to check
   * @return {@code true} if the {@code mfaType} is NONE; {@code false} otherwise
   */
  public static boolean isNone(final MfaType mfaType) {
    return mfaType == NONE;
  }

  /**
   * Checks if the specified {@link MfaType} is AUTHENTICATOR.
   *
   * @param mfaType the MFA type to check
   * @return {@code true} if the {@code mfaType} is AUTHENTICATOR; {@code false} otherwise
   */
  public static boolean isAuthenticator(final MfaType mfaType) {
    return mfaType == AUTHENTICATOR;
  }

  /**
   * Checks if the specified {@link MfaType} is EMAIL.
   *
   * @param mfaType the MFA type to check
   * @return {@code true} if the {@code mfaType} is EMAIL; {@code false} otherwise
   */
  public static boolean isEmail(final MfaType mfaType) {
    return mfaType == EMAIL;
  }

  /**
   * Checks if the specified {@link MfaType} is not AUTHENTICATOR.
   *
   * @param mfaType the MFA type to check
   * @return {@code true} if the {@code mfaType} is not AUTHENTICATOR; {@code false} otherwise
   */
  public static boolean isNotAuthenticator(final MfaType mfaType) {
    return !isAuthenticator(mfaType);
  }

  /**
   * Checks if the specified {@link MfaType} is not NONE.
   *
   * @param mfaType the MFA type to check
   * @return {@code true} if the {@code mfaType} is not NONE; {@code false} otherwise
   */
  public static boolean isNotNone(final MfaType mfaType) {
    return !isNone(mfaType);
  }

  /**
   * Checks if the given MFA type is set to phone.
   *
   * @param mfaType the type of multi-factor authentication
   * @return {@code true} if the MFA type is phone; {@code false} otherwise
   */
  public static boolean isPhone(final MfaType mfaType) {
    return mfaType == PHONE;
  }

}
