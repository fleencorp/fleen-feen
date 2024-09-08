package com.fleencorp.feen.exception.stream;

import com.fleencorp.base.exception.FleenException;

import java.util.Objects;
import java.util.function.Supplier;

import static com.fleencorp.feen.constant.message.ResponseMessage.UNKNOWN;

public class CannotCancelOrDeleteOngoingStreamException extends FleenException {

  private static final String MESSAGE = "Cannot cancel or delete ongoing event or stream. ID: %s";

  public CannotCancelOrDeleteOngoingStreamException(final Object streamId) {
    super(String.format(MESSAGE, Objects.toString(streamId, UNKNOWN)));
  }

  public static Supplier<CannotCancelOrDeleteOngoingStreamException> of(final Object streamId) {
    return () -> new CannotCancelOrDeleteOngoingStreamException(streamId);
  }
}
