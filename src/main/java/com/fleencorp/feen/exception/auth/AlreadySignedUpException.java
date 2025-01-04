package com.fleencorp.feen.exception.auth;

import com.fleencorp.localizer.model.exception.ApiException;

public class AlreadySignedUpException extends ApiException {

  @Override
  public String getMessageCode() {
    return "already.signed.up";
  }

}
