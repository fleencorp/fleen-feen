package com.fleencorp.feen.exception.auth;

import com.fleencorp.localizer.model.exception.ApiException;

import java.util.function.Supplier;

public class UsernameNotFoundException extends ApiException {

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
