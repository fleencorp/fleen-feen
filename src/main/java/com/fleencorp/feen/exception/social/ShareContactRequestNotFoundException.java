package com.fleencorp.feen.exception.social;

import com.fleencorp.localizer.model.exception.ApiException;

import java.util.function.Supplier;

public class ShareContactRequestNotFoundException extends ApiException {

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
