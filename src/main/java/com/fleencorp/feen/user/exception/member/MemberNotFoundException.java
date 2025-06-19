package com.fleencorp.feen.user.exception.member;

import com.fleencorp.localizer.model.exception.LocalizedException;

import java.util.function.Supplier;

public class MemberNotFoundException extends LocalizedException {

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
