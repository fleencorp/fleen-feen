package com.fleencorp.feen.exception.chat.space.member;

import com.fleencorp.base.exception.FleenException;

import java.util.function.Supplier;

public class MemberAlreadyPartOfChatSpaceException extends FleenException {

  @Override
  public String getMessageCode() {
    return "member.already.part.of.chat.space";
  }

  public MemberAlreadyPartOfChatSpaceException(final Object...params) {
    super(params);
  }

  public static Supplier<MemberAlreadyPartOfChatSpaceException> of(final Object memberId) {
    return () -> new MemberAlreadyPartOfChatSpaceException(memberId);
  }
}
