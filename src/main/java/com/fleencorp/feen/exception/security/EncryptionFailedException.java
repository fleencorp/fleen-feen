package com.fleencorp.feen.exception.security;

import com.fleencorp.feen.exception.base.FleenException;

public class EncryptionFailedException extends FleenException {

  public EncryptionFailedException(final String message) {
    super(message);
  }
}
