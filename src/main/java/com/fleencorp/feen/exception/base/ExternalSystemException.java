package com.fleencorp.feen.exception.base;

import static java.lang.String.format;

public class ExternalSystemException extends FleenException {

  public static final String MESSAGE = "Error occurred while communicating with external system: %s";

  public ExternalSystemException(String externalSystemType) {
    super(format(MESSAGE, externalSystemType));
  }
}
