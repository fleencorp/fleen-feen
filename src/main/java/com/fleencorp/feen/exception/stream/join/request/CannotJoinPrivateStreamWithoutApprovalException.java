package com.fleencorp.feen.exception.stream.join.request;

import com.fleencorp.localizer.model.exception.ApiException;

public class CannotJoinPrivateStreamWithoutApprovalException extends ApiException {

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
