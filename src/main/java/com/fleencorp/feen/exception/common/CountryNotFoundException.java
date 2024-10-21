package com.fleencorp.feen.exception.common;

import com.fleencorp.base.exception.FleenException;

import java.util.function.Supplier;

public class CountryNotFoundException extends FleenException {

  @Override
  public String getMessageCode() {
    return "country.not.found";
  }

  public CountryNotFoundException(final Object...params) {
    super(params);
  }

  public static Supplier<CountryNotFoundException> of(final Object countryIdOrCode) {
    return () -> new CountryNotFoundException(countryIdOrCode);
  }
}
