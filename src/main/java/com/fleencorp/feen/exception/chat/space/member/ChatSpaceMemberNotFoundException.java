package com.fleencorp.feen.exception.chat.space.member;

import com.fleencorp.base.exception.FleenException;

import java.util.function.Supplier;

public class ChatSpaceMemberNotFoundException extends FleenException {

  @Override
  public String getMessageCode() {
    return "chat.space.member.not.found";
  }

  public ChatSpaceMemberNotFoundException(final Object...params) {
    super(params);
  }

  public static Supplier<ChatSpaceMemberNotFoundException> of(final Object memberId) {
    return () -> new ChatSpaceMemberNotFoundException(memberId);
  }
}
