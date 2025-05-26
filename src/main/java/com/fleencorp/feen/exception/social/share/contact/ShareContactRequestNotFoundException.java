package com.fleencorp.feen.exception.social.share.contact;

import com.fleencorp.localizer.model.exception.LocalizedException;

import java.util.function.Supplier;

public class ShareContactRequestNotFoundException extends LocalizedException {

  @Override
  public String getMessageCode() {
    return "share.contact.request.not.found";
  }

  public ShareContactRequestNotFoundException(final Object...params) {
    super(params);
  }

  public static Supplier<ShareContactRequestNotFoundException> of(final Object shareContRequestId) {
    return () -> new ShareContactRequestNotFoundException(shareContRequestId);
  }
}
