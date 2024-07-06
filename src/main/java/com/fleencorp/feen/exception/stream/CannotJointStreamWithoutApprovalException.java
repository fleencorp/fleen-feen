package com.fleencorp.feen.exception.stream;

import com.fleencorp.feen.exception.base.FleenException;

import java.util.Objects;

import static com.fleencorp.feen.constant.message.ResponseMessage.UNKNOWN;
import static java.lang.String.format;

public class CannotJointStreamWithoutApprovalException extends FleenException {

  private static final String MESSAGE = "Cannot join stream or event without approval by organizer. ID: %s";

  public CannotJointStreamWithoutApprovalException(final Object streamId) {
    super(format(CannotJointStreamWithoutApprovalException.MESSAGE, Objects.toString(streamId, UNKNOWN)));
  }
}
