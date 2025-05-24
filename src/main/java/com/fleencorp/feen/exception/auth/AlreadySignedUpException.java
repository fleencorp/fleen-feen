package com.fleencorp.feen.exception.auth;

import com.fleencorp.localizer.model.exception.LocalizedException;

public class AlreadySignedUpException extends LocalizedException {

  @Override
  public String getMessageCode() {
    return "already.signed.up";
  }

}
