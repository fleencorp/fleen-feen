package com.fleencorp.feen.country.exception;

import com.fleencorp.localizer.model.exception.LocalizedException;

import java.util.function.Supplier;

public class CountryNotFoundException extends LocalizedException {

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
