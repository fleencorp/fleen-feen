package com.fleencorp.feen.adapter.google.recaptcha.model.enums;

import com.fleencorp.base.constant.base.ApiParameter;
import lombok.Getter;

/**
 * Enum representing parameters used in Google reCAPTCHA API requests.
 * This enum encapsulates parameter names required for interacting with the reCAPTCHA service.
 * Each enum constant represents a specific parameter.
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
@Getter
public enum GoogleRecaptchaParameter implements ApiParameter {

  /**
   * The secret key used for communication with the reCAPTCHA service.
   */
  SECRET("secret"),

  /**
   * The user response token received from the reCAPTCHA widget.
   */
  RESPONSE("response");

  /**
   * The value associated with the enum constant, representing the parameter name.
   */
  private final String value;

  /**
   * Constructor for GoogleRecaptchaParameter enum.
   *
   * @param value The parameter name
   */
  GoogleRecaptchaParameter(String value) {
    this.value = value;
  }
}

