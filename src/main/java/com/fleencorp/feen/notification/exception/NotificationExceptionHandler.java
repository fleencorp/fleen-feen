package com.fleencorp.feen.notification.exception;

import com.fleencorp.feen.common.constant.http.FleenHttpStatus;
import com.fleencorp.feen.notification.exception.core.NotificationNotFoundException;
import com.fleencorp.localizer.model.exception.LocalizedException;
import com.fleencorp.localizer.service.ErrorLocalizer;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestControllerAdvice
public class NotificationExceptionHandler {

  private final ErrorLocalizer localizer;

  public NotificationExceptionHandler(final ErrorLocalizer localizer) {
    this.localizer = localizer;
  }

  @ExceptionHandler(value = {
    NotificationNotFoundException.class,
  })
  @ResponseStatus(value = NOT_FOUND)
  public Object handleNotFound(final LocalizedException e) {
    return localizer.withStatus(e, FleenHttpStatus.notFound());
  }

}
