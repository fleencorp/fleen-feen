package com.fleencorp.feen.exception.auth;

import com.fleencorp.localizer.model.exception.ApiException;

import java.util.function.Supplier;

public class InvalidAuthenticationException extends ApiException {

  @Override
  public String getMessageCode() {
    return "invalid.authentication";
  }

  public InvalidAuthenticationException(final Object...params) {
    super(params);
  }

  public static Supplier<InvalidAuthenticationException> of(final Object emailAddress) {
    return () -> new InvalidAuthenticationException(emailAddress);
  }
}
