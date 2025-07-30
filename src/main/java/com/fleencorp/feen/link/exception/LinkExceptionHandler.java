package com.fleencorp.feen.link.exception;

import com.fleencorp.feen.common.constant.http.FleenHttpStatus;
import com.fleencorp.feen.link.exception.core.InvalidLinkException;
import com.fleencorp.feen.link.exception.core.UnsupportedMusicLinkFormatException;
import com.fleencorp.localizer.model.exception.LocalizedException;
import com.fleencorp.localizer.service.ErrorLocalizer;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RestControllerAdvice(basePackages = {"com.fleencorp.feen.link"})
public class LinkExceptionHandler {

  private final ErrorLocalizer localizer;

  public LinkExceptionHandler(final ErrorLocalizer localizer) {
    this.localizer = localizer;
  }

  @ExceptionHandler(value = {
    InvalidLinkException.class,
    UnsupportedMusicLinkFormatException.class
  })
  @ResponseStatus(value = BAD_REQUEST)
  public Object handleBadRequest(final LocalizedException e) {
    return localizer.withStatus(e, FleenHttpStatus.badRequest());
  }
}
