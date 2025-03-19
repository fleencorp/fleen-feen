package com.fleencorp.feen.constant.security.token;

import com.fleencorp.base.constant.base.ApiParameter;
import lombok.Getter;

/**
 * Enum representing fields commonly found in token claims.
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
@Getter
public enum TokenClaimField implements ApiParameter {

  AUTHENTICATION_STATUS_KEY("authenticationStatus"),
  AUTHORITIES("authorities"),
  FIRST_NAME("firstName"),
  LAST_NAME("lastName"),
  COUNTRY("country"),
  EMAIL_ADDRESS("emailAddress"),
  PHONE_NUMBER("phoneNumber"),
  PROFILE_PHOTO("profilePhoto"),
  STATUS("status"),
  VERIFICATION_STATUS("verificationStatus"),
  TIMEZONE("timezone"),
  TOKEN_TYPE("tokenType"),
  USER_ID("userId"),
  USERNAME("username");

  private final String value;

  TokenClaimField(final String value) {
    this.value = value;
  }
}
