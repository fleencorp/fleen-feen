package com.fleencorp.feen.exception.social;

import com.fleencorp.base.exception.FleenException;

public class ShareContactRequestValueRequiredException extends FleenException {

  @Override
  public String getMessageCode() {
    return "share.contact.request.value.required";
  }
}
