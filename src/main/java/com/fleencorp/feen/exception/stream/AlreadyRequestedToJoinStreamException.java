package com.fleencorp.feen.exception.stream;

import com.fleencorp.base.exception.FleenException;

import java.util.Objects;

import static com.fleencorp.feen.constant.message.ResponseMessage.UNKNOWN;
import static java.lang.String.format;

public class AlreadyRequestedToJoinStreamException extends FleenException {

  private static final String MESSAGE = "You have already requested to join event or stream. Status: %s";

  public AlreadyRequestedToJoinStreamException(final String status) {
    super(format(AlreadyRequestedToJoinStreamException.MESSAGE, Objects.toString(status, UNKNOWN)));
  }
}
