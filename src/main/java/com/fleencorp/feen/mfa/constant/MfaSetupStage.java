package com.fleencorp.feen.mfa.constant;

import lombok.Getter;

@Getter
public enum MfaSetupStage {

  AUTHENTICATOR_VERIFICATION("Authenticator Verification"),
  EMAIL_PHONE_VERIFICATION("Email or Phone Verification"),
  NONE("None");

  private final String value;

  MfaSetupStage(final String value) {
    this.value = value;
  }

  /**
   * Determines the appropriate {@link MfaSetupStage} based on the given {@link MfaType}.
   *
   * <p>This method maps the provided MFA type to a corresponding setup stage. If the MFA type
   * is an authenticator type (as determined by {@link MfaType#isAuthenticator(MfaType)}),
   * it returns {@code AUTHENTICATOR_VERIFICATION}. Otherwise, it returns
   * {@code EMAIL_PHONE_VERIFICATION}.</p>
   *
   * @param type the {@link MfaType} used to determine the setup stage.
   * @return the corresponding {@link MfaSetupStage}, either {@code AUTHENTICATOR_VERIFICATION}
   *         or {@code EMAIL_PHONE_VERIFICATION}, based on the provided MFA type.
   */
  public static MfaSetupStage by(final MfaType type) {
    return MfaType.isAuthenticator(type)
      ? AUTHENTICATOR_VERIFICATION
      : EMAIL_PHONE_VERIFICATION;
  }

}
