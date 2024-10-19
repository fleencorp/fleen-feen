package com.fleencorp.feen.exception.member;

import com.fleencorp.base.exception.FleenException;

import java.util.function.Supplier;

public class MemberNotFoundException extends FleenException {

  @Override
  public String getMessageCode() {
    return "member.not.found";
  }

  public MemberNotFoundException(final Object...params) {
    super(params);
  }

  public static Supplier<MemberNotFoundException> of(final Object memberId) {
    return () -> new MemberNotFoundException(memberId);
  }
}
