package com.fleencorp.feen.exception.base;

import lombok.Getter;

@Getter
public class BasicException extends FleenLocalizedException {

  protected String messageKey = "basic.exception";

  public BasicException(final Object[] messageArgs) {
    this.messageArgs = messageArgs;
  }
}
