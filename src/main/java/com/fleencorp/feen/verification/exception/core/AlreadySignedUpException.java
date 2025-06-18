package com.fleencorp.feen.verification.exception.core;

import com.fleencorp.localizer.model.exception.LocalizedException;

public class AlreadySignedUpException extends LocalizedException {

  @Override
  public String getMessageCode() {
    return "already.signed.up";
  }

}
