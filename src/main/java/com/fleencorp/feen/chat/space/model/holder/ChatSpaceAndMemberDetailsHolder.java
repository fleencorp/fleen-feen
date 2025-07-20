package com.fleencorp.feen.chat.space.model.holder;

import com.fleencorp.feen.chat.space.model.domain.ChatSpace;
import com.fleencorp.feen.chat.space.model.domain.ChatSpaceMember;

public record ChatSpaceAndMemberDetailsHolder(ChatSpace chatSpace, ChatSpaceMember member) {

  public static ChatSpaceAndMemberDetailsHolder of(final ChatSpace chatSpace, final ChatSpaceMember member) {
    return new ChatSpaceAndMemberDetailsHolder(chatSpace, member);
  }
}
