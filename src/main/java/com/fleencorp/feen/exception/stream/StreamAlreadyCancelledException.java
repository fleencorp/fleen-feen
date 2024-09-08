package com.fleencorp.feen.exception.stream;

import com.fleencorp.base.exception.FleenException;

import java.util.Objects;

import static com.fleencorp.feen.constant.message.ResponseMessage.UNKNOWN;
import static java.lang.String.format;

public class StreamAlreadyCancelledException extends FleenException {

  private static final String MESSAGE = "The event or stream has already been cancelled. ID: %s";

  public StreamAlreadyCancelledException(final Object streamId) {
    super(format(StreamAlreadyCancelledException.MESSAGE, Objects.toString(streamId, UNKNOWN)));
  }
}
