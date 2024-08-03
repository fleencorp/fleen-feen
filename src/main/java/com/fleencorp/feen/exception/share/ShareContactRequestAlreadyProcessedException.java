package com.fleencorp.feen.exception.share;

import com.fleencorp.feen.exception.base.FleenException;

public class ShareContactRequestAlreadyProcessedException extends FleenException {

  private static final String MESSAGE = "Share contact request already processed";

  public ShareContactRequestAlreadyProcessedException() {
    super(MESSAGE);
  }
}
