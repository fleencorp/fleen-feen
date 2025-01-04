package com.fleencorp.feen.exception.social;

import com.fleencorp.localizer.model.exception.ApiException;

public class CannotCancelShareContactRequestException extends ApiException {

  @Override
  public String getMessageCode() {
    return "cannot.cancel.share.contact.request";
  }
}
