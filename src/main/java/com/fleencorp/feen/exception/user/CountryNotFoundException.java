package com.fleencorp.feen.exception.user;

import com.fleencorp.base.exception.FleenException;

import java.util.Objects;

import static com.fleencorp.feen.constant.message.ResponseMessage.UNKNOWN;
import static java.lang.String.format;

public class CountryNotFoundException extends FleenException {

  private static final String MESSAGE = "Country does not exist or cannot be found. ID: %s";

  public CountryNotFoundException(final Object countryId) {
    super(format(CountryNotFoundException.MESSAGE, Objects.toString(countryId, UNKNOWN)));
  }
}
