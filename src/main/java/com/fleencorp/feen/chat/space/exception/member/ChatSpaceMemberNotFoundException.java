package com.fleencorp.feen.chat.space.exception.member;

import com.fleencorp.localizer.model.exception.LocalizedException;

import java.util.function.Supplier;

public class ChatSpaceMemberNotFoundException extends LocalizedException {

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
