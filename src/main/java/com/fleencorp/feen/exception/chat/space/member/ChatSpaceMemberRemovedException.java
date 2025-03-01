package com.fleencorp.feen.exception.chat.space.member;

import com.fleencorp.localizer.model.exception.ApiException;

public class ChatSpaceMemberRemovedException extends ApiException {

  @Override
  public String getMessageCode() {
    return "chat.space.member.removed";
  }

  public ChatSpaceMemberRemovedException(final Object...params) {
    super(params);
  }

  public static ChatSpaceMemberRemovedException of(final Object memberId) {
    return new ChatSpaceMemberRemovedException(memberId);
  }
}
