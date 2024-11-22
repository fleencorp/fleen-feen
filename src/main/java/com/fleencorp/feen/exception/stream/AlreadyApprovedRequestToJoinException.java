package com.fleencorp.feen.exception.stream;

import com.fleencorp.base.exception.FleenException;

public class AlreadyApprovedRequestToJoinException extends FleenException {

  @Override
  public String getMessageCode() {
    return "already.approved.requested.to.join";
  }

  public AlreadyApprovedRequestToJoinException(final Object...params) {
    super(params);
  }

  public static AlreadyApprovedRequestToJoinException of(final Object status) {
    return new AlreadyApprovedRequestToJoinException(status);
  }
}
