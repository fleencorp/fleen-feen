package com.fleencorp.feen.user.exception;

import com.fleencorp.feen.constant.http.FleenHttpStatus;
import com.fleencorp.feen.user.exception.authentication.InvalidAuthenticationException;
import com.fleencorp.feen.user.exception.authentication.InvalidAuthenticationTokenException;
import com.fleencorp.feen.user.exception.member.MemberNotFoundException;
import com.fleencorp.feen.user.exception.recaptcha.InvalidReCaptchaException;
import com.fleencorp.feen.user.exception.user.*;
import com.fleencorp.localizer.model.exception.LocalizedException;
import com.fleencorp.localizer.model.response.ErrorResponse;
import com.fleencorp.localizer.service.ErrorLocalizer;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice(basePackages = {"com.fleencorp.feen.user"})
public class UserExceptionHandler {

  private final ErrorLocalizer localizer;

  public UserExceptionHandler(final ErrorLocalizer localizer) {
    this.localizer = localizer;
  }

  @ExceptionHandler(value = {
    BannedAccountException.class,
    DisabledAccountException.class,
    UpdatePasswordFailedException.class,
    UpdateProfileInfoFailedException.class
  })
  @ResponseStatus(value = BAD_REQUEST)
  public ErrorResponse handleBadRequest(final LocalizedException e) {
    return localizer.withStatus(e, FleenHttpStatus.badRequest());
  }

  @ExceptionHandler(value = {
    InvalidReCaptchaException.class,
  })
  @ResponseStatus(value = BAD_REQUEST)
  public ErrorResponse handleExternalBadRequest(final LocalizedException e) {
    return localizer.withStatus(e, FleenHttpStatus.badRequest());
  }

  @ExceptionHandler(value = {
    EmailAddressAlreadyExistsException.class,
    PhoneNumberAlreadyExistsException.class,
  })
  @ResponseStatus(value = CONFLICT)
  public ErrorResponse handleConflict(final LocalizedException e) {
    return localizer.withStatus(e, FleenHttpStatus.conflict());
  }

  @ExceptionHandler(value = {
    MemberNotFoundException.class,
    UserNotFoundException.class,
  })
  @ResponseStatus(value = NOT_FOUND)
  public Object handleNotFound(final LocalizedException e) {
    return localizer.withStatus(e, FleenHttpStatus.notFound());
  }

  @ExceptionHandler(value = {
    InvalidAuthenticationException.class,
    InvalidAuthenticationTokenException.class,
  })
  @ResponseStatus(value = UNAUTHORIZED)
  public ErrorResponse handleUnauthorized(final LocalizedException e) {
    return localizer.withStatus(e, FleenHttpStatus.unauthorized());
  }
}
