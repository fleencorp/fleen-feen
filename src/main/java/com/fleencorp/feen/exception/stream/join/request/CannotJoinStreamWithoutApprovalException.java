package com.fleencorp.feen.exception.stream.join.request;

import com.fleencorp.localizer.model.exception.ApiException;

public class CannotJoinStreamWithoutApprovalException extends ApiException {

  @Override
  public String getMessageCode() {
    return "cannot.join.stream.with.approval";
  }

  public CannotJoinStreamWithoutApprovalException(final Object...params) {
    super(params);
  }

  public static CannotJoinStreamWithoutApprovalException of(final Object streamId) {
    return new CannotJoinStreamWithoutApprovalException(streamId);
  }
}
