package com.fleencorp.feen.role.exception;

import com.fleencorp.feen.common.constant.http.FleenHttpStatus;
import com.fleencorp.feen.role.exception.core.NoRoleAvailableToAssignException;
import com.fleencorp.localizer.model.exception.LocalizedException;
import com.fleencorp.localizer.model.response.ErrorResponse;
import com.fleencorp.localizer.service.ErrorLocalizer;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RestControllerAdvice(basePackages = {"com.fleencorp.feen.role"})
public class RoleExceptionHandler {

  private final ErrorLocalizer localizer;

  public RoleExceptionHandler(final ErrorLocalizer localizer) {
    this.localizer = localizer;
  }

  @ExceptionHandler(value = {
    NoRoleAvailableToAssignException.class
  })
  @ResponseStatus(value = BAD_REQUEST)
  public ErrorResponse handleBadRequest(final LocalizedException e) {
    return localizer.withStatus(e, FleenHttpStatus.badRequest());
  }
}
