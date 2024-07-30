package com.fleencorp.feen.exception.security.recaptcha;

import com.fleencorp.feen.exception.base.FleenException;
import org.apache.commons.lang3.StringUtils;

import java.io.Serial;

/**
 * <p>The InvalidReCaptchaException class is used to define the exception response message when an invalid captcha token is submitted.
 * </p>
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
public class InvalidReCaptchaException extends FleenException {

  @Serial
  private static final long serialVersionUID = 1L;

  public static final String DEFAULT_MESSAGE = "Invalid ReCaptcha!!!";

  public InvalidReCaptchaException(final String message) {
    super(StringUtils.isEmpty(message) ? DEFAULT_MESSAGE : message);
  }

  public InvalidReCaptchaException() {
    super(DEFAULT_MESSAGE);
  }
}
