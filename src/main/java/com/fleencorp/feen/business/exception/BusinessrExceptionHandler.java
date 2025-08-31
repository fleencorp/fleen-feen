package com.fleencorp.feen.business.exception;

import com.fleencorp.feen.common.constant.http.FleenHttpStatus;
import com.fleencorp.localizer.model.exception.LocalizedException;
import com.fleencorp.localizer.model.response.ErrorResponse;
import com.fleencorp.localizer.service.ErrorLocalizer;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestControllerAdvice(basePackages = {"com.fleencorp.feen.calendar"})
public class BusinessrExceptionHandler {

  private final ErrorLocalizer localizer;

  public BusinessrExceptionHandler(final ErrorLocalizer localizer) {
    this.localizer = localizer;
  }

  @ExceptionHandler(value = {
    BusinessNotOwnerException.class
  })
  @ResponseStatus(value = CONFLICT)
  public ErrorResponse handleConflict(final LocalizedException e) {
    return localizer.withStatus(e, FleenHttpStatus.conflict());
  }

  @ExceptionHandler(value = {
    BusinessNotFoundException.class
  })
  @ResponseStatus(value = NOT_FOUND)
  public Object handleNotFound(final LocalizedException e) {
    return localizer.withStatus(e, FleenHttpStatus.notFound());
  }
}
