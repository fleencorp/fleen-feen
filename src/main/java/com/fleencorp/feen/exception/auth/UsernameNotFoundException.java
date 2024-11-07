package com.fleencorp.feen.exception.auth;

import com.fleencorp.base.exception.FleenException;

import java.util.function.Supplier;

public class UsernameNotFoundException extends FleenException {

  @Override
  public String getMessageCode() {
    return "username.not.found";
  }

  public UsernameNotFoundException(Object...params) {
    super(params);
  }

  public static Supplier<UsernameNotFoundException> of(final Object emailAddress) {
    return () -> new UsernameNotFoundException(emailAddress);
  }
}
