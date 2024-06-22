package com.fleencorp.feen.exception.security;

import com.fleencorp.feen.exception.base.FleenException;

public class DecryptionFailedException extends FleenException {

  public DecryptionFailedException(String message) {
    super(message);
  }
}
