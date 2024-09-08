package com.fleencorp.feen.exception.share.contact;

import com.fleencorp.base.exception.FleenException;

import java.util.Objects;

import static com.fleencorp.feen.constant.message.ResponseMessage.UNKNOWN;

public class ContactNotFoundException extends FleenException {

  private static final String MESSAGE = "Contact cannot be found or does not exist. ID: %s";

  public ContactNotFoundException(final Object contactId) {
    super(String.format(MESSAGE, Objects.toString(contactId, UNKNOWN)));
  }
}
