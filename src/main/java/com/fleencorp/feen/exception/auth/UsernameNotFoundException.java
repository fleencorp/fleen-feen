package com.fleencorp.feen.exception.auth;

import com.fleencorp.localizer.model.exception.LocalizedException;

import java.util.function.Supplier;

public class UsernameNotFoundException extends LocalizedException {

  @Override
  public String getMessageCode() {
    return "username.not.found";
  }

  public UsernameNotFoundException(final Object...params) {
    super(params);
  }

  public static Supplier<UsernameNotFoundException> of(final Object emailAddress) {
    return () -> new UsernameNotFoundException(emailAddress);
  }
}
