package com.fleencorp.feen.constant.security.mfa;

import com.fleencorp.base.constant.base.ApiParameter;
import lombok.Getter;

@Getter
public enum MfaSetupStage implements ApiParameter {

  AUTHENTICATOR_VERIFICATION("Authenticator Verification"),
  EMAIL_PHONE_VERIFICATION("Email or Phone Verification"),
  NONE("None");

  private final String value;

  MfaSetupStage(final String value) {
    this.value = value;
  }

  public static MfaSetupStage by(MfaType type) {
    return MfaType.isAuthenticator(type)
      ? AUTHENTICATOR_VERIFICATION
      : EMAIL_PHONE_VERIFICATION;
  }

}
