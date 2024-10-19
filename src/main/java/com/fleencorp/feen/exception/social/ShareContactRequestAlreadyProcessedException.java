package com.fleencorp.feen.exception.social;

import com.fleencorp.base.exception.FleenException;

public class ShareContactRequestAlreadyProcessedException extends FleenException {

  private static final String MESSAGE = "Share contact request already processed";

  public ShareContactRequestAlreadyProcessedException() {
    super(MESSAGE);
  }
}
