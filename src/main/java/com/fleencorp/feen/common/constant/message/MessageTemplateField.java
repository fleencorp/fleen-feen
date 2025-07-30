package com.fleencorp.feen.common.constant.message;

import com.fleencorp.base.constant.base.ApiParameter;
import lombok.Getter;

/**
 * Enumeration representing the fields used in message templates.
 *
 * <p>Each enum constant corresponds to a specific field that can be used in a message template,
 * such as first name, last name, email address, etc.</p>
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
@Getter
public enum MessageTemplateField implements ApiParameter {

  FIRST_NAME("firstName"),
  VERIFICATION_CODE("verificationCode"),
  COMMENT("comment"),
  EMAIL_ADDRESS("emailAddress"),
  LAST_NAME("lastName"),
  PHONE_NUMBER("phoneNumber"),
  PROFILE_VERIFICATION_STATUS("profileVerificationStatus"),
  TITLE("title"),
  LOGO("logo.png");

  private final String value;

  MessageTemplateField(final String value) {
    this.value = value;
  }
}
