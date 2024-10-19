package com.fleencorp.feen.exception.user;

import com.fleencorp.base.exception.FleenException;

import java.util.function.Supplier;

public class UserNotFoundException extends FleenException {

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
