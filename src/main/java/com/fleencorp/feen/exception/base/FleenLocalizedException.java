package com.fleencorp.feen.exception.base;

import lombok.Getter;

@Getter
public class FleenLocalizedException extends RuntimeException {

  protected String messageKey;
  protected Object[] messageArgs = new Object[100];
}
