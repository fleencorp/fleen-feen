package com.fleencorp.feen.exception.user.profile;

import com.fleencorp.localizer.model.exception.ApiException;

import java.util.function.Supplier;

public class PhoneNumberAlreadyExistsException extends ApiException {

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
