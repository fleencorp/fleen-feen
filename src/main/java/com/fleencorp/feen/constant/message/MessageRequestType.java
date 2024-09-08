package com.fleencorp.feen.constant.message;

import com.fleencorp.base.constant.base.ApiParameter;
import lombok.Getter;

/**
 * Enumeration representing different types of message requests.
 *
 * <p>Each enum constant corresponds to a specific message request type and holds a
 * string value representing a human-readable description of that type.</p>
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
@Getter
public enum MessageRequestType implements ApiParameter {

  COMPLETED_USER_SIGNUP("Completed User Sign Up"),
  FORGOT_PASSWORD("Forgot Password"),
  MFA_SETUP_VERIFICATION("MFA Setup Verification"),
  MFA_VERIFICATION("MFA Verification"),
  PROFILE_UPDATE_SUCCESS("Profile Update Success"),
  PROFILE_UPDATE_VERIFICATION("Profile Update Verification"),
  RESET_PASSWORD_SUCCESS("Reset Password Success"),
  SIGNUP_VERIFICATION("Sign Up Verification"),
  STREAM_EVENT_CREATION("Stream Event Creation");

  private final String value;

  MessageRequestType(final String value) {
    this.value = value;
  }
}
