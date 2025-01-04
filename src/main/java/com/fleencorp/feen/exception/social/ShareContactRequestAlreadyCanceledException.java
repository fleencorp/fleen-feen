package com.fleencorp.feen.exception.social;

import com.fleencorp.localizer.model.exception.ApiException;

public class ShareContactRequestAlreadyCanceledException extends ApiException {

  @Override
  public String getMessageCode() {
    return "share.contact.request.canceled";
  }
}
