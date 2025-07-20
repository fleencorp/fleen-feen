package com.fleencorp.feen.contact.exception;

import com.fleencorp.feen.common.constant.http.FleenHttpStatus;
import com.fleencorp.feen.contact.exception.core.ContactNotFoundException;
import com.fleencorp.localizer.model.exception.LocalizedException;
import com.fleencorp.localizer.service.ErrorLocalizer;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestControllerAdvice(basePackages = {"com.fleencorp.feen.contact"})
public class ContactExceptionHandler {

  private final ErrorLocalizer localizer;

  public ContactExceptionHandler(final ErrorLocalizer localizer) {
    this.localizer = localizer;
  }

  @ExceptionHandler(value = {
    ContactNotFoundException.class
  })
  @ResponseStatus(value = NOT_FOUND)
  public Object handleNotFound(final LocalizedException e) {
    return localizer.withStatus(e, FleenHttpStatus.notFound());
  }
}
