package com.fleencorp.feen.exception.social;

import com.fleencorp.localizer.model.exception.ApiException;

public class ShareContactRequestAlreadyProcessedException extends ApiException {

  @Override
  public String getMessageCode() {
    return "share.contact.request.already.processed";
  }
}
