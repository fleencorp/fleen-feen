package com.fleencorp.feen.exception.social.contact;

import com.fleencorp.base.exception.FleenException;

import java.util.function.Supplier;

public class ContactNotFoundException extends FleenException {

  @Override
  public String getMessageCode() {
    return "contact.not.found";
  }

  public ContactNotFoundException(final Object...params) {
    super(params);
  }

  public static Supplier<ContactNotFoundException> of(final Object contactId) {
    return () -> new ContactNotFoundException(contactId);
  }
}
