package com.fleencorp.feen.stream.exception.request;

import com.fleencorp.localizer.model.exception.LocalizedException;

public class CannotJoinPrivateStreamWithoutApprovalException extends LocalizedException {

  @Override
  public String getMessageCode() {
    return "cannot.join.private.stream.without.approval";
  }

  public CannotJoinPrivateStreamWithoutApprovalException(final Object...params) {
    super(params);
  }

  public static CannotJoinPrivateStreamWithoutApprovalException of(final Object streamId) {
    return new CannotJoinPrivateStreamWithoutApprovalException(streamId);
  }
}
