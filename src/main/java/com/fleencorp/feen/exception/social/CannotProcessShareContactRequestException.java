package com.fleencorp.feen.exception.social;

import com.fleencorp.localizer.model.exception.ApiException;

public class CannotProcessShareContactRequestException extends ApiException {

  @Override
  public String getMessageCode() {
    return "cannot.process.share.contact.request";
  }
}
