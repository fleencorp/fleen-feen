package com.fleencorp.feen.constant.message;

import lombok.Getter;

/**
 * Enum representing common message details including template names and message titles.
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
@Getter
public enum CommonMessageDetails {

  SIGN_UP_VERIFICATION( "sign-up-verification", "Complete Sign Up"),
  SIGN_UP_COMPLETED("sign-up-completed", "Sign Up Completed"),
  MFA_VERIFICATION("mfa-verification", "Complete Sign in"),
  MFA_SETUP("mfa-setup", "Complete Multi-Factor Authentication (MFA) Setup"),
  FORGOT_PASSWORD("forgot-password", "Forgot Password"),
  RESET_PASSWORD_SUCCESSFUL("reset-password-success", "Password Reset Successful"),
  PROFILE_UPDATE_VERIFICATION("profile-update-verification", "Profile Update Verification"),
  PRE_ONBOARDING("pre-onboarding", "Complete Onboarding");

  private final String templateName;
  private final String messageTitle;

  CommonMessageDetails(String templateName, String messageTitle) {
    this.templateName = templateName;
    this.messageTitle = messageTitle;
  }
}
