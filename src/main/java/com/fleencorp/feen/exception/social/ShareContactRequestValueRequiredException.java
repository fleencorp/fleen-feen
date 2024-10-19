package com.fleencorp.feen.exception.social;

import com.fleencorp.base.exception.FleenException;

public class ShareContactRequestValueRequiredException extends FleenException {

  private static final String MESSAGE = "Share contact request value is required";

  public ShareContactRequestValueRequiredException() {
    super(MESSAGE);
  }
}
