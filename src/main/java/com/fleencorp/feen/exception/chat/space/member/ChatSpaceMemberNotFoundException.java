package com.fleencorp.feen.exception.chat.space.member;

import com.fleencorp.localizer.model.exception.ApiException;

import java.util.function.Supplier;

public class ChatSpaceMemberNotFoundException extends ApiException {

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
