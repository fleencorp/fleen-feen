package com.fleencorp.feen.user.exception.user;

import com.fleencorp.localizer.model.exception.LocalizedException;

import java.util.function.Supplier;

public class PhoneNumberAlreadyExistsException extends LocalizedException {

  @Override
  public String getMessageCode() {
    return "phone.number.already.exists";
  }

  public PhoneNumberAlreadyExistsException(final Object...params) {
    super(params);
  }

  public static Supplier<PhoneNumberAlreadyExistsException> of(final Object phoneNumber) {
    return () -> new PhoneNumberAlreadyExistsException(phoneNumber);
  }
}
