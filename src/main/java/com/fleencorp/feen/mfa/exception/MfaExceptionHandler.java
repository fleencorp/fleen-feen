package com.fleencorp.feen.mfa.exception;

import com.fleencorp.feen.common.constant.http.FleenHttpStatus;
import com.fleencorp.feen.mfa.exception.core.MfaGenerationFailedException;
import com.fleencorp.feen.mfa.exception.core.MfaVerificationFailed;
import com.fleencorp.localizer.model.exception.LocalizedException;
import com.fleencorp.localizer.model.response.ErrorResponse;
import com.fleencorp.localizer.service.ErrorLocalizer;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RestControllerAdvice(basePackages = {"com.fleencorp.feen.mfa"})
public class MfaExceptionHandler {

  private final ErrorLocalizer localizer;

  public MfaExceptionHandler(final ErrorLocalizer localizer) {
    this.localizer = localizer;
  }

  @ExceptionHandler(value = {
    MfaGenerationFailedException.class,
    MfaVerificationFailed.class,
  })
  @ResponseStatus(value = BAD_REQUEST)
  public ErrorResponse handleBadRequest(final LocalizedException e) {
    return localizer.withStatus(e, FleenHttpStatus.badRequest());
  }
}
