package com.fleencorp.feen.exception.social.contact;

import com.fleencorp.localizer.model.exception.LocalizedException;

import java.util.function.Supplier;

public class ContactNotFoundException extends LocalizedException {

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
