package com.fleencorp.feen.adapter.google.recaptcha.model.enums;

import com.fleencorp.base.constant.base.EndpointBlock;
import lombok.Getter;

/**
 * Enum representing endpoint blocks used in Google reCAPTCHA API requests.
 * This enum encapsulates endpoint paths required for interacting with the reCAPTCHA service.
 * Each enum constant represents a specific endpoint block.
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
@Getter
public enum GoogleRecaptchaEndpointBlock implements EndpointBlock {

  /**
   * The endpoint block for the reCAPTCHA site verify endpoint.
   */
  SITE_VERIFY("/siteverify");

  /**
   * The value associated with the enum constant, representing the endpoint path.
   */
  private final String value;

  /**
   * Constructor for GoogleRecaptchaEndpointBlock enum.
   *
   * @param value The endpoint path
   */
  GoogleRecaptchaEndpointBlock(final String value) {
    this.value = value;
  }

  @Override
  public String value() {
    return this.value;
  }
}

