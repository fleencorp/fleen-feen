package com.fleencorp.feen.exception.auth;

import com.fleencorp.base.exception.FleenException;

public class AlreadySignedUpException extends FleenException {

  @Override
  public String getMessageCode() {
    return "already.signed.up";
  }

}
