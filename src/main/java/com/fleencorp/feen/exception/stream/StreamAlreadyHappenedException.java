package com.fleencorp.feen.exception.stream;

import com.fleencorp.base.exception.FleenException;

import java.util.Objects;

import static com.fleencorp.feen.constant.message.ResponseMessage.UNKNOWN;
import static java.lang.String.format;

public class StreamAlreadyHappenedException extends FleenException {

  private static final String MESSAGE = "Stream or event has already happened. ID: %s, End Date: %s";

  public StreamAlreadyHappenedException(final Object streamId, final Object endDate) {
    super(format(StreamAlreadyHappenedException.MESSAGE, Objects.toString(streamId, UNKNOWN), Objects.toString(endDate, UNKNOWN)));
  }
}
