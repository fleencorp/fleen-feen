package com.fleencorp.feen.exception.social;

import com.fleencorp.localizer.model.exception.ApiException;

public class ShareContactRequestValueRequiredException extends ApiException {

  @Override
  public String getMessageCode() {
    return "share.contact.request.value.required";
  }
}
