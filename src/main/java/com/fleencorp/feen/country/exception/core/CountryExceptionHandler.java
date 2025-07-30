package com.fleencorp.feen.country.exception.core;

import com.fleencorp.feen.common.constant.http.FleenHttpStatus;
import com.fleencorp.feen.country.exception.CountryNotFoundException;
import com.fleencorp.localizer.model.exception.LocalizedException;
import com.fleencorp.localizer.service.ErrorLocalizer;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestControllerAdvice(basePackages = {"com.fleencorp.feen.country"})
public class CountryExceptionHandler {

  private final ErrorLocalizer localizer;

  public CountryExceptionHandler(final ErrorLocalizer localizer) {
    this.localizer = localizer;
  }

  @ExceptionHandler(value = {
    CountryNotFoundException.class
  })
  @ResponseStatus(value = NOT_FOUND)
  public Object handleNotFound(final LocalizedException e) {
    return localizer.withStatus(e, FleenHttpStatus.notFound());
  }
}
