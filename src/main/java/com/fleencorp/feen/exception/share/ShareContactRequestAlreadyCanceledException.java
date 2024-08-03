package com.fleencorp.feen.exception.share;

import com.fleencorp.feen.exception.base.FleenException;

public class ShareContactRequestAlreadyCanceledException extends FleenException {

  private static final String MESSAGE = "Share contact request already canceled";

  public ShareContactRequestAlreadyCanceledException() {
    super(MESSAGE);
  }
}
