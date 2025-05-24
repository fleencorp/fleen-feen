package com.fleencorp.feen.exception.social;

import com.fleencorp.localizer.model.exception.LocalizedException;

public class ShareContactRequestAlreadyProcessedException extends LocalizedException {

  @Override
  public String getMessageCode() {
    return "share.contact.request.already.processed";
  }
}
