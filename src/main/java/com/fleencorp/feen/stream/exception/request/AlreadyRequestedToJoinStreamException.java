package com.fleencorp.feen.stream.exception.request;

import com.fleencorp.localizer.model.exception.LocalizedException;

public class AlreadyRequestedToJoinStreamException extends LocalizedException {

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
