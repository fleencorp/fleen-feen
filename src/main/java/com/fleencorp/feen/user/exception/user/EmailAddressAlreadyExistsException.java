package com.fleencorp.feen.user.exception.user;

import com.fleencorp.localizer.model.exception.LocalizedException;

import java.util.function.Supplier;

public class EmailAddressAlreadyExistsException extends LocalizedException {

  @Override
  public String getMessageCode() {
    return "email.address.already.exists";
  }

  public EmailAddressAlreadyExistsException(final Object...params) {
    super(params);
  }

  public static Supplier<EmailAddressAlreadyExistsException> of(final Object emailAddress) {
    return () -> new EmailAddressAlreadyExistsException(emailAddress);
  }
}
