package com.fleencorp.feen.exception.social;

import com.fleencorp.base.exception.FleenException;

public class CannotCancelShareContactRequestException extends FleenException {

  @Override
  public String getMessageCode() {
    return "cannot.cancel.share.contact.request";
  }
}
