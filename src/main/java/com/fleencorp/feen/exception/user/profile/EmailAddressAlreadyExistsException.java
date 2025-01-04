package com.fleencorp.feen.exception.user.profile;

import com.fleencorp.localizer.model.exception.ApiException;

import java.util.function.Supplier;

public class EmailAddressAlreadyExistsException extends ApiException {

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
