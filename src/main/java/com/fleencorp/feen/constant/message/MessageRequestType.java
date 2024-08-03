package com.fleencorp.feen.constant.message;

import com.fleencorp.base.constant.base.ApiParameter;
import lombok.Getter;

@Getter
public enum MessageRequestType implements ApiParameter {

  COMPLETED_USER_SIGNUP("Completed User Sign Up"),
  FORGOT_PASSWORD("Forgot Password"),
  MFA_SETUP_VERIFICATION("MFA Setup Verification"),
  MFA_VERIFICATION("MFA Verification"),
  PROFILE_UPDATE_VERIFICATION("Profile Update Verification"),
  RESET_PASSWORD_SUCCESS("Reset Password Success"),
  SIGNUP_VERIFICATION("Sign Up Verification");

  private final String value;

  MessageRequestType(String value) {
    this.value = value;
  }
}
