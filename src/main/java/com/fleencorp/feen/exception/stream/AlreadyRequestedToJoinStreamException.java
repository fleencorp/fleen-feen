package com.fleencorp.feen.exception.stream;

import com.fleencorp.base.exception.FleenException;

public class AlreadyRequestedToJoinStreamException extends FleenException {

  @Override
  public String getMessageCode() {
    return "already.requested.to.join";
  }

  public AlreadyRequestedToJoinStreamException(final Object...params) {
    super(params);
  }

  public static AlreadyRequestedToJoinStreamException of(final Object status) {
    return new AlreadyRequestedToJoinStreamException(status);
  }
}
