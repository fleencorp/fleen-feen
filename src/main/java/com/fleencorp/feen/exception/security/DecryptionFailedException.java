package com.fleencorp.feen.exception.security;

import com.fleencorp.feen.exception.base.FleenException;

public class DecryptionFailedException extends FleenException {

  public DecryptionFailedException(final String message) {
    super(message);
  }
}
