package com.fleencorp.feen.exception.chat.space.member;

import com.fleencorp.localizer.model.exception.ApiException;

import java.util.function.Supplier;

public class ChatSpaceMemberRemovedException extends ApiException {

  @Override
  public String getMessageCode() {
    return "chat.space.member.removed";
  }

  public ChatSpaceMemberRemovedException(final Object...params) {
    super(params);
  }

  public static Supplier<ChatSpaceMemberRemovedException> of(final Object memberId) {
    return () -> new ChatSpaceMemberRemovedException(memberId);
  }
}
