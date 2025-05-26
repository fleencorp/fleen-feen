package com.fleencorp.feen.exception.social.share.contact;

import com.fleencorp.localizer.model.exception.LocalizedException;

public class CannotProcessShareContactRequestException extends LocalizedException {

  @Override
  public String getMessageCode() {
    return "cannot.process.share.contact.request";
  }
}
