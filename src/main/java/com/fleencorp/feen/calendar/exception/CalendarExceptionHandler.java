package com.fleencorp.feen.calendar.exception;

import com.fleencorp.feen.calendar.exception.core.CalendarAlreadyActiveException;
import com.fleencorp.feen.calendar.exception.core.CalendarAlreadyExistException;
import com.fleencorp.feen.calendar.exception.core.CalendarNotFoundException;
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
public class CalendarExceptionHandler {

  private final ErrorLocalizer localizer;

  public CalendarExceptionHandler(final ErrorLocalizer localizer) {
    this.localizer = localizer;
  }

  @ExceptionHandler(value = {
    CalendarAlreadyActiveException.class,
    CalendarAlreadyExistException.class,
  })
  @ResponseStatus(value = CONFLICT)
  public ErrorResponse handleConflict(final LocalizedException e) {
    return localizer.withStatus(e, FleenHttpStatus.conflict());
  }

  @ExceptionHandler(value = {
    CalendarNotFoundException.class
  })
  @ResponseStatus(value = NOT_FOUND)
  public Object handleNotFound(final LocalizedException e) {
    return localizer.withStatus(e, FleenHttpStatus.notFound());
  }
}
