package com.fleencorp.feen.exception.auth;

import com.fleencorp.base.exception.FleenException;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class AlreadySignedUpException extends FleenException {

  @Override
  public String getMessageCode() {
    return "already.signed.up";
  }

  public AlreadySignedUpException() {
    super();
  }

}
