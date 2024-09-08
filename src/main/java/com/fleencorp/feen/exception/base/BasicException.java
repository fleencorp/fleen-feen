package com.fleencorp.feen.exception.base;

import com.fleencorp.base.exception.FleenException;
import lombok.Getter;

@Getter
public class BasicException extends FleenException {

  protected String messageCode = "basic.exception";

  public BasicException(final Object[]...params) {
    this.params = params;
  }
}
