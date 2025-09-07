package com.fleencorp.feen.chat.space.exception.member;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fleencorp.localizer.model.exception.LocalizedException;

public class ChatSpaceMemberRemovedException extends LocalizedException {

  @Override
  @JsonIgnore
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
