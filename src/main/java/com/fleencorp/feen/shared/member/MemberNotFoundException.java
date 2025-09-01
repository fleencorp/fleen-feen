package com.fleencorp.feen.shared.member;

import com.fleencorp.localizer.model.exception.LocalizedException;

import java.util.function.Supplier;

public class MemberNotFoundException extends LocalizedException {

  public MemberNotFoundException(Object...params) {
    super(params);
  }

  @Override
  public String getMessageCode() {
    return "member.not.found";
  }

  public static Supplier<MemberNotFoundException> of(Object memberId) {
    return () -> new MemberNotFoundException(memberId);
  }
}
