package com.fleencorp.feen.exception.stream;

import com.fleencorp.base.exception.FleenException;

public class CannotJointStreamWithoutApprovalException extends FleenException {

  @Override
  public String getMessageCode() {
    return "cannot.join.stream.with.approval";
  }

  public CannotJointStreamWithoutApprovalException(final Object...params) {
    super(params);
  }

  public static CannotJointStreamWithoutApprovalException of(final Object streamId) {
    return new CannotJointStreamWithoutApprovalException(streamId);
  }
}
