package com.fleencorp.feen.exception.share;

import com.fleencorp.base.exception.FleenException;

import java.util.Objects;

import static com.fleencorp.feen.constant.message.ResponseMessage.UNKNOWN;

public class ShareContactRequestNotFoundException extends FleenException {

  private static final String MESSAGE = "Share contact request not found. ID: %s";

  public ShareContactRequestNotFoundException(final Object shareContactRequestId) {
    super(String.format(MESSAGE, Objects.toString(shareContactRequestId, UNKNOWN)));
  }
}
