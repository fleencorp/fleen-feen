package com.fleencorp.feen.exception.social.share.contact;

import com.fleencorp.localizer.model.exception.LocalizedException;

public class CannotCancelShareContactRequestException extends LocalizedException {

  @Override
  public String getMessageCode() {
    return "cannot.cancel.share.contact.request";
  }
}
