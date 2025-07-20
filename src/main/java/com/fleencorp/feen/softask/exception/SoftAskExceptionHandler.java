package com.fleencorp.feen.softask.exception;

import com.fleencorp.feen.common.constant.http.FleenHttpStatus;
import com.fleencorp.feen.softask.exception.core.SoftAskAnswerNotFoundException;
import com.fleencorp.feen.softask.exception.core.SoftAskNotFoundException;
import com.fleencorp.feen.softask.exception.core.SoftAskReplyNotFoundException;
import com.fleencorp.feen.softask.exception.core.SoftAskUpdateDeniedException;
import com.fleencorp.localizer.model.exception.LocalizedException;
import com.fleencorp.localizer.model.response.ErrorResponse;
import com.fleencorp.localizer.service.ErrorLocalizer;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;

@RestControllerAdvice(basePackages = {"com.fleencorp.feen.role"})
public class SoftAskExceptionHandler {

  private final ErrorLocalizer localizer;

  public SoftAskExceptionHandler(final ErrorLocalizer localizer) {
    this.localizer = localizer;
  }

  @ExceptionHandler(value = {
    SoftAskNotFoundException.class,
    SoftAskAnswerNotFoundException.class,
    SoftAskReplyNotFoundException.class
  })
  @ResponseStatus(value = BAD_REQUEST)
  public ErrorResponse handleBadRequest(final LocalizedException e) {
    return localizer.withStatus(e, FleenHttpStatus.badRequest());
  }

  @ExceptionHandler(value = {
    SoftAskUpdateDeniedException.class
  })
  @ResponseStatus(value = FORBIDDEN)
  public ErrorResponse handleForbidden(final LocalizedException e) {
    return localizer.withStatus(e, FleenHttpStatus.forbidden());
  }
}
