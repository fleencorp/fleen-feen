package com.fleencorp.feen.user.security.recaptcha;

import com.fleencorp.localizer.model.exception.LocalizedException;

import java.util.Objects;

/**
 * <p>The InvalidReCaptchaException class is used to define the exception response message when an invalid captcha token is submitted.</p>
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
public class InvalidReCaptchaException extends LocalizedException {

  public static final String DEFAULT_MESSAGE = "Invalid ReCaptcha!!!";

  public InvalidReCaptchaException(final String message) {
    super(Objects.toString(message, DEFAULT_MESSAGE));
  }

  public InvalidReCaptchaException() {
    super(DEFAULT_MESSAGE);
  }
}
