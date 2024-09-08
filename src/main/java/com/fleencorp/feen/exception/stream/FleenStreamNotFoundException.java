package com.fleencorp.feen.exception.stream;

import com.fleencorp.base.exception.FleenException;

import java.util.Objects;

import static com.fleencorp.feen.constant.message.ResponseMessage.UNKNOWN;
import static java.lang.String.format;

public class FleenStreamNotFoundException extends FleenException {

  private static final String MESSAGE = "Stream or event does not exist or cannot be found. ID: %s";

  public FleenStreamNotFoundException(final Object streamId) {
    super(format(FleenStreamNotFoundException.MESSAGE, Objects.toString(streamId, UNKNOWN)));
  }
}
