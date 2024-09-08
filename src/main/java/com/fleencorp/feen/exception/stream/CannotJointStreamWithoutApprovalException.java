package com.fleencorp.feen.exception.stream;

import com.fleencorp.base.exception.FleenException;

import java.util.Objects;

import static com.fleencorp.feen.constant.message.ResponseMessage.UNKNOWN;
import static java.lang.String.format;

public class CannotJointStreamWithoutApprovalException extends FleenException {

  private static final String MESSAGE = "Cannot join event or stream without approval by organizer. ID: %s";

  public CannotJointStreamWithoutApprovalException(final Object streamId) {
    super(format(CannotJointStreamWithoutApprovalException.MESSAGE, Objects.toString(streamId, UNKNOWN)));
  }
}
