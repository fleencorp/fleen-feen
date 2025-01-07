package com.fleencorp.feen.exception.user;

import com.fleencorp.localizer.model.exception.ApiException;

import java.util.function.Supplier;

public class UserNotFoundException extends ApiException {

  @Override
  public String getMessageCode() {
    return "user.not.found";
  }

  public UserNotFoundException(final Object...params) {
    super(params);
  }

  public static Supplier<UserNotFoundException> of(final Object memberId) {
    return () -> new UserNotFoundException(memberId);
  }
}
