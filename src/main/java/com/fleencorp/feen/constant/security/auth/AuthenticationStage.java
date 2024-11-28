package com.fleencorp.feen.constant.security.auth;

import com.fleencorp.base.constant.base.ApiParameter;
import lombok.Getter;

/**
 * Represents the stages in the authentication process.
 * This enum defines the various stages a user may go through during authentication,
 * including stages such as pre-verification and multi-factor authentication (MFA).
 */
@Getter
public enum AuthenticationStage implements ApiParameter {

  NONE("None"),
  PRE_VERIFICATION("Pre Verification"),
  MFA_VERIFICATION("Multi Factor or Two FA or Pre Authentication");

  private final String value;

  AuthenticationStage(final String value) {
    this.value = value;
  }

  public static AuthenticationStage preVerification() {
    return PRE_VERIFICATION;
  }
}
