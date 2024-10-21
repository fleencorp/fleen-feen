package com.fleencorp.feen.exception.social;

import com.fleencorp.base.exception.FleenException;

public class CannotProcessShareContactRequestException extends FleenException {

  @Override
  public String getMessageCode() {
    return "cannot.process.share.contact.request";
  }
}
