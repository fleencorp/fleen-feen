package com.fleencorp.feen.user.exception.user;

import com.fleencorp.localizer.model.exception.LocalizedException;

import java.util.function.Supplier;

public class UserNotFoundException extends LocalizedException {

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
