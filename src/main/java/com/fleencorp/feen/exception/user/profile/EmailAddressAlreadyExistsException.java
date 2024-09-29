package com.fleencorp.feen.exception.user.profile;

import com.fleencorp.base.exception.FleenException;

import java.util.function.Supplier;

public class EmailAddressAlreadyExistsException extends FleenException {

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
