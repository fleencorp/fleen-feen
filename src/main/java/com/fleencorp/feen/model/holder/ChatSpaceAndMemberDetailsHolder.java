package com.fleencorp.feen.model.holder;

import com.fleencorp.feen.model.domain.chat.ChatSpace;
import com.fleencorp.feen.model.domain.chat.ChatSpaceMember;

public record ChatSpaceAndMemberDetailsHolder(ChatSpace chatSpace, ChatSpaceMember member) {

  public static ChatSpaceAndMemberDetailsHolder of(final ChatSpace chatSpace, final ChatSpaceMember member) {
    return new ChatSpaceAndMemberDetailsHolder(chatSpace, member);
  }
}
