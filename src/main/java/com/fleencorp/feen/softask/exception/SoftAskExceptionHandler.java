package com.fleencorp.feen.softask.exception;

import com.fleencorp.feen.common.exception.FailedOperationException;
import com.fleencorp.feen.softask.exception.core.SoftAskNotFoundException;
import com.fleencorp.feen.softask.exception.core.SoftAskParentNotFoundException;
import com.fleencorp.feen.softask.exception.core.SoftAskReplyNotFoundException;
import com.fleencorp.feen.softask.exception.core.SoftAskUpdateDeniedException;
import com.fleencorp.localizer.model.exception.LocalizedException;
import com.fleencorp.localizer.model.response.ErrorResponse;
import com.fleencorp.localizer.service.ErrorLocalizer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.fleencorp.feen.common.constant.http.FleenHttpStatus.*;
import static org.springframework.http.HttpStatus.*;

@Slf4j
@RestControllerAdvice(basePackages =
  {"com.fleencorp.feen.softask"}
)
public class SoftAskExceptionHandler {

  private final ErrorLocalizer localizer;

  public SoftAskExceptionHandler(final ErrorLocalizer localizer) {
    this.localizer = localizer;
  }

  @ExceptionHandler(value = {
    SoftAskNotFoundException.class,
    SoftAskReplyNotFoundException.class
  })
  @ResponseStatus(value = NOT_FOUND)
  public ErrorResponse handleNotFound(final LocalizedException e) {
    log.info(e.getMessage());
    return localizer.withStatus(e, notFound());
  }

  @ExceptionHandler(value = {
    FailedOperationException.class,
    SoftAskParentNotFoundException.class
  })
  @ResponseStatus(value = BAD_REQUEST)
  public ErrorResponse handleBadRequest(final LocalizedException e) {
    log.info(e.getMessage());
    return localizer.withStatus(e, badRequest());
  }

  @ExceptionHandler(value = {
    SoftAskUpdateDeniedException.class
  })
  @ResponseStatus(value = FORBIDDEN)
  public ErrorResponse handleForbidden(final LocalizedException e) {
    log.info(e.getMessage());
    return localizer.withStatus(e, forbidden());
  }

  @ExceptionHandler(value = {
    Exception.class
  })
  @ResponseStatus(value = INTERNAL_SERVER_ERROR)
  public ErrorResponse handleInternal(final Exception e) {
    log.info(e.getMessage());
    return localizer.withStatus(ErrorResponse.defaultMessageCode(), internalServerError());
  }
}
