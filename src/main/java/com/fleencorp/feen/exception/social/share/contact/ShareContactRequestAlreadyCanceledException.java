package com.fleencorp.feen.exception.social.share.contact;

import com.fleencorp.localizer.model.exception.LocalizedException;

public class ShareContactRequestAlreadyCanceledException extends LocalizedException {

  @Override
  public String getMessageCode() {
    return "share.contact.request.canceled";
  }
}
