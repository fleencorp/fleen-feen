package com.fleencorp.feen.oauth2.exception;

import com.fleencorp.feen.constant.http.FleenHttpStatus;
import com.fleencorp.feen.oauth2.exception.core.Oauth2InvalidAuthorizationException;
import com.fleencorp.feen.oauth2.exception.core.Oauth2InvalidGrantOrTokenException;
import com.fleencorp.feen.oauth2.exception.core.Oauth2InvalidScopeException;
import com.fleencorp.localizer.model.exception.LocalizedException;
import com.fleencorp.localizer.model.response.ErrorResponse;
import com.fleencorp.localizer.service.ErrorLocalizer;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RestControllerAdvice(basePackages = {"com.fleencorp.feen.oauth2"})
public class Oauth2ExceptionHandler {

  private final ErrorLocalizer localizer;

  public Oauth2ExceptionHandler(final ErrorLocalizer localizer) {
    this.localizer = localizer;
  }

  @ExceptionHandler(value = {
    Oauth2InvalidAuthorizationException.class,
    Oauth2InvalidGrantOrTokenException.class,
    Oauth2InvalidScopeException.class,
  })
  @ResponseStatus(value = BAD_REQUEST)
  public ErrorResponse handleExternalBadRequest(final LocalizedException e) {
    return localizer.withStatus(e, FleenHttpStatus.badRequest());
  }

}
