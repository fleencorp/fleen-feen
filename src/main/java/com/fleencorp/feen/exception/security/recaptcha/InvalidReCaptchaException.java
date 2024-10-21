package com.fleencorp.feen.exception.security.recaptcha;

import com.fleencorp.base.exception.FleenException;

import java.util.Objects;

/**
 * <p>The InvalidReCaptchaException class is used to define the exception response message when an invalid captcha token is submitted.</p>
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
public class InvalidReCaptchaException extends FleenException {

  public static final String MESSAGE = "Invalid ReCaptcha!!!";

  public InvalidReCaptchaException(final String message) {
    super(Objects.toString(message, MESSAGE));
  }

  public InvalidReCaptchaException() {
    super(MESSAGE);
  }
}
